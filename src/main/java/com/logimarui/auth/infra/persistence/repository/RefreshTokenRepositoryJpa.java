package com.logimarui.auth.infra.persistence.repository;


import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.repository.RefreshTokenRepository;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;
import com.logimarui.auth.infra.persistence.jpa.RefreshTokenJpaRepository;
import com.logimarui.auth.infra.persistence.mapper.RefreshTokenMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class RefreshTokenRepositoryJpa implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository jpa;
    private final SessionRepository sessionRepository;

    @Override
    public Optional<RefreshToken> findValidByTokenHash(String tokenHash) {
        return Optional.empty();
    }

    @Override
    public RefreshToken save(RefreshToken token, Session session) {
        RefreshTokenEntity entity =
                jpa.findBySessionId(session.getId())
                        .map(e -> {
                            e.setTokenHash(token.getTokenHash());
                            e.setExpiresAt(token.getExpiresAt());
                            e.setRefreshTokenStatus(token.getRefreshTokenStatus());
                            return e;
                        })
                        .orElseGet(() -> RefreshTokenMapper.toEntity(token));
        RefreshTokenEntity saved = jpa.save(entity);
        return RefreshTokenMapper.toDomain(saved, session);
    }

    @Override
    public void revokeBySessionId(Long sessionId) {

    }

    @Override
    public Optional<RefreshToken> findBySessionIdAndRefreshTokenStatus(Long sessionId, RefreshTokenStatus refreshTokenStatus) {
        return jpa.findBySessionIdAndRefreshTokenStatus(
                sessionId,
                refreshTokenStatus
        ).flatMap(entity ->
                sessionRepository.findById(entity.getSessionId())
                        .map(session ->
                                RefreshTokenMapper.toDomain(entity, session)));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash)
                .filter(this::isValid)
                .flatMap(entity ->
                        sessionRepository.findById(entity.getSessionId())
                                .map(session ->
                                        RefreshTokenMapper.toDomain(entity, session)));
    }

    private boolean isValid(RefreshTokenEntity entity) {
        return entity.getRefreshTokenStatus() == RefreshTokenStatus.ACTIVE
                && entity.getExpiresAt().isAfter(Instant.now());
    }

}
