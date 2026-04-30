package com.logimarui.authentication.infra.persistence.mapper;

import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.infra.persistence.entity.SessionEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SessionMapper {
    public static Session toDomain(SessionEntity entity) {
        return Session.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getDeviceId(),
                entity.getLastIpAddress(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getLoggedOutAt(),
                entity.getSessionStatus()
        );
    }

    public static SessionEntity toEntity(Session session) {
        return new SessionEntity(
                session.getId(),
                session.getUserId(),
                session.getDeviceId(),
                session.getLastIpAddress(),
                session.getCreatedAt(),
                session.getExpiresAt(),
                session.getLoggedOutAt(),
                session.getSessionStatus()
        );
    }
}
