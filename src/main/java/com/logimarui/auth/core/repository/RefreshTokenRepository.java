package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findValidByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken token, Session session);

    void revokeBySessionId(Long sessionId);
}