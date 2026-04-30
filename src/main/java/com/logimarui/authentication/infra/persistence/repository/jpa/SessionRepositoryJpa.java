package com.logimarui.authentication.infra.persistence.repository.jpa;


import com.logimarui.authentication.core.domain.enums.SessionStatus;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.repository.SessionRepository;
import com.logimarui.authentication.infra.persistence.entity.SessionEntity;
import com.logimarui.authentication.infra.persistence.jpa.SessionJpaRepository;
import com.logimarui.authentication.infra.persistence.mapper.SessionMapper;
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
    public Optional<Session> findByUserIdAndDeviceIdAndSessionStatus(Long userId, String deviceId, SessionStatus sessionStatus) {

        return jpa.findByUserIdAndDeviceIdAndSessionStatus(userId, deviceId, sessionStatus)
                .map(SessionMapper::toDomain);
    }


}
