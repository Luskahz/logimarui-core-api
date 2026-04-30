package com.logimarui.auth.core.application.services;

import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.port.*;
import com.logimarui.auth.core.repository.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UtilsServices {

    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    protected void updateSessionLastIpAddressIfChanged(@NotNull Session session, String ip, Instant now) {
        if (!Objects.equals(session.getLastIpAddress(), ip)) {
            session.updateIpAddress(ip, now);
            sessionRepository.save(session);
        }
    }

    protected void logoutSession(@NotNull Session session, Instant now) {
        if (session.isLoggedOut()) return;
        session.logout(now);
        sessionRepository.save(session);
    }
    protected Optional<RefreshToken> findActiveRefreshTokenBySession(@NotNull Session session) {
        return refreshTokenRepository.findBySessionIdAndRefreshTokenStatus(session.getId(), RefreshTokenStatus.ACTIVE);
    }
}
