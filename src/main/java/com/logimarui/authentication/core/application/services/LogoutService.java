package com.logimarui.auth.core.application.services;

import com.logimarui.auth.core.domain.enums.SessionStatus;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.port.*;
import com.logimarui.auth.core.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LogoutService {

    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UtilsServices utilsServices;

    @Transactional
    public void logout(Long userId, String ip, String deviceId) {
        Instant now = Instant.now();

        Optional<Session> sessionOpt =
                sessionRepository.findByUserIdAndDeviceIdAndSessionStatus(userId, deviceId, SessionStatus.ACTIVE);

        if (sessionOpt.isEmpty()) {
            return;
        }

        Session session = sessionOpt.get();

        if (!session.isLoggedOut()) {
            session.updateIpAddress(ip, now);
            utilsServices.logoutSession(session, now);
        }

        utilsServices.findActiveRefreshTokenBySession(session)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token, session);
                });
    }

}
