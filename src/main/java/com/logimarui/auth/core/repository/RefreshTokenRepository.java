package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findValidByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken token, Session session);

    void revokeBySessionId(Long sessionId);

    Optional<RefreshTokenEntity> findBySessionIdAndRefreshTokenStatus(Long sessionId, RefreshTokenStatus refreshTokenStatus);
}