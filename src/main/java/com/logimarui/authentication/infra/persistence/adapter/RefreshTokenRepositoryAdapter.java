package com.logimarui.authentication.infra.persistence.adapter;

import com.logimarui.authentication.core.domain.model.RefreshToken;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.repository.RefreshTokenRepository;
import com.logimarui.authentication.infra.persistence.jpa.RefreshTokenJpaRepository;
import com.logimarui.authentication.infra.persistence.jpa.SessionJpaRepository;
import com.logimarui.authentication.infra.persistence.mapper.RefreshTokenMapper;
import com.logimarui.authentication.infra.persistence.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpa;
    private final SessionJpaRepository sessionJpa;

    @Override
    public RefreshToken save(RefreshToken token, Session session) {
        var entity = jpa.findBySessionId(session.getId())
                .map(e -> {
                    e.setTokenHash(token.getTokenHash());
                    e.setExpiresAt(token.getExpiresAt());
                    e.setStatus(token.getStatus());
                    e.setReplacedByTokenId(token.getReplacedByTokenId());
                    return e;
                })
                .orElseGet(() -> RefreshTokenMapper.toEntity(token));

        var saved = jpa.save(entity);
        return RefreshTokenMapper.toDomain(saved, session);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash)
                .flatMap(entity ->
                        sessionJpa.findById(entity.getSessionId())
                                .map(sessionEntity ->
                                        RefreshTokenMapper.toDomain(entity, SessionMapper.toDomain(sessionEntity))
                                )
                );
    }

}