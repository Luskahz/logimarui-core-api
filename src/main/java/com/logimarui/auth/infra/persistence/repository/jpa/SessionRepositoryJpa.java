package com.logimarui.auth.infra.persistence.repository.jpa;


import com.logimarui.auth.core.domain.enums.SessionStatus;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.infra.persistence.entity.SessionEntity;
import com.logimarui.auth.infra.persistence.jpa.SessionJpaRepository;
import com.logimarui.auth.infra.persistence.mapper.SessionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class SessionRepositoryJpa implements SessionRepository {
    private final SessionJpaRepository jpa;

    @Override
    public List<Session> findByUserIdAndSessionStatus(Long userId, SessionStatus sessionStatus) {
        return jpa.findByUserIdAndSessionStatus(userId, sessionStatus)
                .stream()
                .map(SessionMapper::toDomain)
                .toList();
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
