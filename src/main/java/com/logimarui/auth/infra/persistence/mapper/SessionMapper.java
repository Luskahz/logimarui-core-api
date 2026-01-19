package com.logimarui.auth.infra.persistence.mapper;

import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.infra.persistence.entity.SessionEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SessionMapper {
    public static Session toDomain(SessionEntity entity) {
        Session session = new Session(
                entity.getId(),
                entity.getUserId(),
                entity.getDevice(),
                entity.getLastIpAddress(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
        if (!entity.isActive()) {
            session.deactivate();
        }
        return session;
    }


    public static SessionEntity toEntity(Session session) {
        SessionEntity entity = new SessionEntity(
                session.getUserId(),
                session.isValid(session.getCreatedAt()),
                session.getDevice(),
                session.getLastIpAddress(),
                session.getCreatedAt(),
                session.getExpiresAt()
        );

        return entity;
    }

    public static void copyState(Session session, SessionEntity entity) {
        entity.setActive(session.isValid(session.getCreatedAt()));
        entity.setLastIpAddress(session.getLastIpAddress());
    }
}
