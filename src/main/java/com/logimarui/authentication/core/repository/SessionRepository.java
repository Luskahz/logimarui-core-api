package com.logimarui.authentication.core.repository;

import com.logimarui.authentication.core.domain.model.Session;

import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);
    Optional<Session> findById(Long id);
    Optional<Session> findActiveByUserId(Long userId);
}
