package com.logimarui.auth.core.service;

import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.refresh.RefreshRequestDTO;
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
import com.logimarui.auth.infra.security.principal.UserPrincipal;
import com.logimarui.auth.infra.security.token.TokenGenerator;
import com.logimarui.auth.infra.security.token.TokenHashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
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

    @Transactional public AuthTokens login(@NotNull LoginRequestDTO request, String ip, String deviceId) {
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

        IssuedRefreshToken issuedRefreshToken = refreshTokenRegister(session);

        return new AuthTokens(
                issuedRefreshToken.rawToken(),
                jwtService.generateAccessToken(user, session),
                jwtService.getAccessTokenExpiresInSeconds()
                );
    }
    @Transactional public AuthTokens register(RegisterRequestDTO request, String ip, String deviceid) {
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
    @Transactional public AuthContext me(@NotNull Authentication authentication, String ip, String deviceId) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        User user = getUserForAuthentication(principal.getUserId());
        Session session = findSessionByUserAndDeviceId(user,deviceId)
                .orElseThrow(()-> new SecurityException("Session not found"));
        long expiresInSeconds = Math.max(
                Duration.between(Instant.now(), principal.getAccessTokenExpiresAt()).getSeconds(),
                0
        );

        return new AuthContext (
                principal.getUserId(),
                roles,
                principal.getSessionId(),
                !session.isInvalid(Instant.now()),
                expiresInSeconds
        );
    }

    @Transactional public AuthTokenResponseDTO refresh(@NotNull RefreshRequestDTO request, String ip, String deviceIp){
        RefreshToken refreshToken = findRefreshTokenByToken(request.refreshToken())
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));
        if(refreshToken.getSession().getLastIpAddress()!= ip) {
            ;
        }
    }

    public void logout(String accessToken) {
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
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    public User getUserForAuthentication(Long userId) {
        User user = getUserById(userId);

        if (user.isBlocked()) {
            throw new IllegalStateException("Blocked user");
        }

        if (!user.canAuthenticate()) {
            throw new IllegalStateException("User cannot authenticate");
        }

        return user;
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
    public Optional<RefreshToken> findRefreshTokenByToken(@NotNull String refreshToken){
        String tokenHash = tokenHashService.hash(refreshToken);
        return refreshTokenRepository.findByTokenHash(tokenHash);

    }
    public RefreshToken updateLastIpAddressFromRefreshToken(RefreshToken refreshToken, String ipAddress){
        return refreshTokenRepository.updateLastIpAdress(refreshToken, ipAddress);
    }



    public Optional<Session> findSessionById(Long sessionId){
        return sessionRepository.findById(sessionId);
    }
    public Optional<RefreshToken> findActiveRefreshTokenBySession(@NotNull Session session){
        return refreshTokenRepository.findBySessionIdAndRefreshTokenStatus(session.getId(), RefreshTokenStatus.ACTIVE);
    }

}
