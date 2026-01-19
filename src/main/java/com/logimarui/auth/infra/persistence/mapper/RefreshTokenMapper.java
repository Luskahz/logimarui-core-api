package com.logimarui.auth.infra.persistence.mapper;

import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenMapper {


    public static RefreshToken toDomain(
            RefreshTokenEntity entity,
            Session session
    ) {
        RefreshToken token = new RefreshToken(
                session,
                entity.getTokenHash(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );

        if (entity.isRevoked()) {
            token.revoke();
        }

        return token;
    }

    public static RefreshTokenEntity toEntity(RefreshToken token) {
        return new RefreshTokenEntity(
                token.getSession().getId(),
                token.getTokenHash(),
                token.getCreatedAt(),
                token.getExpiresAt()
        );
    }

    public static void copyState(
            RefreshToken token,
            RefreshTokenEntity entity
    ) {
        entity.setRevoked(token.isRevoked());
    }
}
