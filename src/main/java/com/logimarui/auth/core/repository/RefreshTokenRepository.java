package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findValidByTokenHash(String tokenHash);

    void save(RefreshToken token);

    void revokeBySessionId(Long sessionId);
}