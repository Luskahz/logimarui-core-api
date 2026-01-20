package com.logimarui.auth.infra.persistence.repository;


import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.repository.RefreshTokenRepository;
import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;
import com.logimarui.auth.infra.persistence.jpa.RefreshTokenJpaRepository;
import com.logimarui.auth.infra.persistence.mapper.RefreshTokenMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class RefreshTokenRepositoryJpa implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository jpa;

    @Override
    public Optional<RefreshToken> findValidByTokenHash(String tokenHash) {
        return Optional.empty();
    }

    @Override
    public void save(RefreshToken token) {
        RefreshTokenEntity saved = jpa.save(RefreshTokenMapper.toEntity(token));
        return RefreshTokenMapper.toDomain(saved);
    }

    @Override
    public void revokeBySessionId(Long sessionId) {

    }
}
