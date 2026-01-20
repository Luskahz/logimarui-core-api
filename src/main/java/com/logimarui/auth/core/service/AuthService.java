package com.logimarui.auth.core.service;

import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.repository.RefreshTokenRepository;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.core.repository.UserRepository;
import com.logimarui.auth.infra.security.jwt.JwtService;
import com.logimarui.auth.infra.security.token.TokenGenerator;
import com.logimarui.auth.infra.security.token.TokenHashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    public AuthTokenResponseDTO login(LoginRequestDTO request, String ip, String device) {
        throw new UnsupportedOperationException("TODO");
    }

    public User userRegister(RegisterRequestDTO request, String ip){
        return userRepository.save(
                User.create(
                        request.username(),
                        passwordEncoder.encode(request.password()),
                        request.matricula()
                )
        );
    }
    public Session sessionRegister(User user, String device, String ip){
        return sessionRepository.save(
                Session.create(
                        user.getId(),
                        ip,
                        device
                )
        );
    }
    public String refreshTokenRegister(Session session) {
        String rawRefreshToken = tokenGenerator.generate();
        String refreshTokenHash = tokenHashService.hash(rawRefreshToken);

        refreshTokenRepository.save(
                RefreshToken.create(session, refreshTokenHash),
                session
        );

        return rawRefreshToken;
    }

    @Transactional
    public AuthTokens register(
            RegisterRequestDTO request,
            String ip,
            String device
    ) {
        User user = userRegister(request, ip);
        Session session = sessionRegister(user, device, ip);
        String rawRefreshToken = refreshTokenRegister(session);
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
}
