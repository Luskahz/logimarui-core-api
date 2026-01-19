package com.logimarui.auth.infra.persistence.jpa;

import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.repository.RefreshTokenRepository;

import java.util.Optional;

public class RefreshTokenJpaRepository implements RefreshTokenRepository {
    @Override
    public Optional<RefreshToken> findValidByTokenHash(String tokenHash) {
        return Optional.empty();
    }

    @Override
    public void save(RefreshToken token) {

    }

    @Override
    public void revokeBySessionId(Long sessionId) {

    }
}
