package com.logimarui.auth.infra.persistence.repository;


import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.infra.persistence.entity.SessionEntity;
import com.logimarui.auth.infra.persistence.jpa.SessionJpaRepository;
import com.logimarui.auth.infra.persistence.mapper.SessionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class SessionRepositoryJpa implements SessionRepository {
    private final SessionJpaRepository jpa;

    @Override
    public Optional<Session> findActiveByUserId(Long userId) {
        return jpa.findByUserIdAndActiveTrue(userId)
                .map(SessionMapper::toDomain);
    }

    @Override
    public Optional<Session> findById(Long sessionId) {
        return jpa.findById(sessionId)
                .map(SessionMapper::toDomain);
    }

    @Override
    public Session save(Session session) {
        SessionEntity saved = jpa.save(SessionMapper.toEntity(session));
        return SessionMapper.toDomain(saved);
    }

    @Override
    public Optional<Session> findByUserIdAndDeviceId(Long userId, String deviceId) {

        return jpa.findByUserIdAndDeviceId(userId, deviceId)
                .map(SessionMapper::toDomain);
    }

}
