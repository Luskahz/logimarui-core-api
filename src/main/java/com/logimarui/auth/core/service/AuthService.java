package com.logimarui.auth.core.service;

import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.exception.UserNotFoundException;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.repository.RefreshTokenRepository;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.core.repository.UserRepository;
import com.logimarui.auth.infra.persistence.mapper.RefreshTokenMapper;
import com.logimarui.auth.infra.security.jwt.JwtService;
import com.logimarui.auth.infra.security.token.TokenGenerator;
import com.logimarui.auth.infra.security.token.TokenHashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;

    @Transactional
    public AuthTokenResponseDTO login(@NotNull LoginRequestDTO request, String ip, String deviceId) {
        User user = userRepository.findByEmployeeId(request.employeeId())
                .orElseThrow(() ->
                    new UserNotFoundException("User not found for provided employee number")
                );

        Session session = findSessionByUserAndDeviceId(user, deviceId)
                .map(existingSession -> {
                    if(existingSession.isActive(Instant.now())){
                        existingSession.updateIpAddress(ip);
                        return existingSession;
                    }
                    return sessionRegister(
                            user, deviceId, ip
                    );
                })
                .orElseGet(() -> sessionRegister(user, deviceId, ip));

        findActiveRefreshTokenBySession(session)
                .ifPresent(RefreshToken::revoke);
        IssuedRefreshToken issuedRefreshToken = refreshTokenRegister(session);

        return new AuthTokenResponseDTO(
                issuedRefreshToken.rawToken(),
                jwtService.generateAccessToken(user, session),
                jwtService.getAccessTokenExpiresInSeconds()
                );
    }

    @Transactional
    public AuthTokens register(RegisterRequestDTO request, String ip, String deviceid) {
        User user = userRegister(request, ip);
        Session session = sessionRegister(user, deviceid, ip);

        IssuedRefreshToken issued = refreshTokenRegister(session);
        String rawRefreshToken = issued.rawToken();
        String accessToken = jwtService.generateAccessToken(user, session);

        return new AuthTokens(
                rawRefreshToken,
                accessToken,
                jwtService.getAccessTokenExpiresInSeconds()
        );
    }

    public AuthTokenResponseDTO refresh(String refreshToken) {
        throw new UnsupportedOperationException("TODO");
    }
    public void logout(String accessToken) {
        throw new UnsupportedOperationException("TODO");
    }
    public void me(String accessToken) {
        throw new UnsupportedOperationException("TODO");
    }


    public User userRegister(@NotNull RegisterRequestDTO request, String ip){
        return userRepository.save(
                User.create(
                        request.username(),
                        passwordEncoder.encode(request.password()),
                        request.employeeId()
                )
        );
    }
    public Session sessionRegister(@NotNull User user, String deviceId, String ip){

        return sessionRepository.save(
                Session.create(
                        user.getId(),
                        ip,
                        deviceId
                )
        );
    }
    public IssuedRefreshToken refreshTokenRegister(@NotNull Session session) {
        String raw= tokenGenerator.generate();
        String hash = tokenHashService.hash(raw);

        RefreshToken token = RefreshToken.create(session, hash);
        RefreshToken saved = refreshTokenRepository.save(token, session);

        return new IssuedRefreshToken(saved, raw);

    }
    public Optional<Session> findSessionByUserAndDeviceId(@NotNull User user, String deviceId){
        return sessionRepository.findByUserIdAndDeviceId(user.getId(), deviceId);
    }
    public Optional<RefreshToken> findActiveRefreshTokenBySession(@NotNull Session session){
        return refreshTokenRepository.findBySessionIdAndRefreshTokenStatus(session.getId(), RefreshTokenStatus.ACTIVE)
                        .map(entity -> RefreshTokenMapper.toDomain(entity, session));
    }


}
