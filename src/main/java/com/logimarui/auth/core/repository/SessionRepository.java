package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.model.Session;

import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findActiveByUserId(Long userId);

    Optional<Session> findById(Long sessionId);

    void save(Session session);

    void deactivateAllByUserId(Long userId);
}
