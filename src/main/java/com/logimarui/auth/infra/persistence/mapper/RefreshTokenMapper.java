package com.logimarui.auth.infra.persistence.mapper;

import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenMapper {

    public static RefreshToken toDomain(
            RefreshTokenEntity entity,
            Session session
    ) {
        return RefreshToken.reconstitute(
                entity.getId(),
                session,
                entity.getTokenHash(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.isRevoked()
        );
    }

    public static RefreshTokenEntity toEntity(RefreshToken token) {
        return new RefreshTokenEntity(
                token.getId(),
                token.getSession().getId(),
                token.getTokenHash(),
                token.isRevoked(),
                token.getCreatedAt(),
                token.getExpiresAt()
        );
    }
}

