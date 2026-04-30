package com.logimarui.authentication.core.repository;

import com.logimarui.authentication.core.domain.enums.SessionStatus;
import com.logimarui.authentication.core.domain.model.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> findByUserIdAndSessionStatus(Long userId, SessionStatus sessionStatus);
    Optional<Session> findById(Long sessionId);
    Session save(Session session);
    Optional<Session> findByUserIdAndDeviceIdAndSessionStatus(Long userId, String deviceId, SessionStatus sessionStatus);

}
