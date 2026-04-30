package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findValidByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken token, Session session);

    void revokeBySessionId(Long sessionId);

    Optional<RefreshToken> findBySessionIdAndRefreshTokenStatus(Long sessionId, RefreshTokenStatus refreshTokenStatus);
    Optional<RefreshToken> findByTokenHash(String tokenHash);

}