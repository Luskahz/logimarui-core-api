package com.logimarui.auth.core.application.services;

import com.logimarui.auth.core.application.results.AuthTokens;
import com.logimarui.auth.core.application.results.IssuedRefreshToken;
import com.logimarui.auth.core.domain.enums.SessionStatus;
import com.logimarui.auth.core.domain.exception.UserNotFoundException;
import com.logimarui.auth.core.domain.model.IssuedAccessToken;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.port.*;
import com.logimarui.auth.core.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthTimeProperties authTokenProperties;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;


    @Transactional public AuthTokens login(@NotNull Long employeeId, String password, String ip, String deviceId) {
        Instant now = Instant.now();
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found for provided employee number")
                );
        user.assertCanAuthenticate();

        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            user.registerFailedLogin();
            userRepository.save(user);
            throw new SecurityException("wrong password");
        }

        Session session = findSessionByUserAndDeviceIdAndSessionStatus(user, deviceId, SessionStatus.ACTIVE)
                .map(existingSession -> {
                    if(existingSession.isValid(now)){
                        existingSession.updateIpAddress(ip, now);
                        sessionRepository.save(existingSession);
                        return existingSession;
                    }
                    return sessionRegister(
                            user, deviceId, ip
                    );
                })
                .orElseGet(() -> sessionRegister(user, deviceId, ip));



        IssuedRefreshToken issuedRefreshToken = refreshTokenRegister(session);
        IssuedAccessToken issuedAccessToken = jwtService.generateAccessToken(user, session);
        long expiresInSeconds = Math.max(Duration.between(now, issuedAccessToken.expiresAt()).getSeconds(), 0);

        user.recordSuccessfulLogin(now);
        userRepository.save(user);
        return new AuthTokens(
                issuedRefreshToken.rawToken(),
                issuedAccessToken.token(),
                expiresInSeconds
        );
    }

    public Optional<Session> findSessionByUserAndDeviceIdAndSessionStatus(@NotNull User user, String deviceId, SessionStatus sessionStatus){
        return sessionRepository.findByUserIdAndDeviceIdAndSessionStatus(user.getId(), deviceId, sessionStatus);
    }
    public Session sessionRegister(@NotNull User user, String deviceId, String ip){
        return sessionRepository.save(
                Session.create(
                        user.getId(),
                        ip,
                        deviceId,
                        authTokenProperties.sessionTtl()
                )
        );
    }
    public IssuedRefreshToken refreshTokenRegister(@NotNull Session session) {
        String raw= tokenGenerator.generate();
        String hash = tokenHashService.hash(raw);

        RefreshToken token = RefreshToken.create(session, hash,authTokenProperties.refreshTokenTtl());
        RefreshToken saved = refreshTokenRepository.save(token, session);

        return new IssuedRefreshToken(saved, raw);

    }
}
