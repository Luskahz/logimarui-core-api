package com.logimarui.auth.infra.persistence.jpa;

import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.repository.SessionRepository;

import java.util.Optional;

public class SessionJpaRepository implements SessionRepository {
    @Override
    public Optional<Session> findActiveByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<Session> findById(Long sessionId) {
        return Optional.empty();
    }

    @Override
    public void save(Session session) {

    }

    @Override
    public void deactivateAllByUserId(Long userId) {

    }
}
