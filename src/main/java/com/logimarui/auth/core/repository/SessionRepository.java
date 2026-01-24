package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.enums.SessionStatus;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> findByUserIdAndSessionStatus(Long userId, SessionStatus sessionStatus);
    Optional<Session> findById(Long sessionId);
    Session save(Session session);
    Optional<Session> findByUserIdAndDeviceId(Long userId, String deviceId);

}
