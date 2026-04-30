package com.logimarui.auth.core.application.services;

import com.logimarui.auth.core.application.results.AuthContext;
import com.logimarui.auth.core.domain.exception.UserNotFoundException;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.port.*;
import com.logimarui.auth.core.repository.RefreshTokenRepository;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UtilsServices utilsServices;

    @Transactional
    public AuthContext me(
            @NotNull Long userId,
            @NotNull Long sessionId,
            Instant accessTokenExpiresAt,
            String ip,
            String deviceId
    ) {
        Instant now = Instant.now();

        userRepository.findById(userId)
                .map(User::assertCanAuthenticate)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        Session session = findSessionById(sessionId)
                .orElseThrow(()-> new SecurityException("Session not found"));
        if (!Objects.equals(session.getUserId(), userId)
                || !Objects.equals(session.getDeviceId(), deviceId)) {
            throw new SecurityException("Session does not belong to this user/device");
        }

        utilsServices.updateSessionLastIpAddressIfChanged(session, ip, now);

        return new AuthContext (
                userId,
                sessionId,
                session.isValid(now),
                getAccessTokenRemainingSeconds(now, accessTokenExpiresAt)
        );
    }

    public Optional<Session> findSessionById(Long sessionId){
        return sessionRepository.findById(sessionId);
    }
    private long getAccessTokenRemainingSeconds(Instant now, Instant accessTokenExpiresAt) {
        return Math.max(
                Duration.between(now, accessTokenExpiresAt).getSeconds(),
                0
        );
    }
}
