package com.logimarui.auth.infra.persistence.mapper;

import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.infra.persistence.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getUsername(),
                entity.getCreatedAt(),
                entity.getMatricula(),
                entity.getPasswordHash(),
                entity.isActive(),
                entity.isLocked(),
                entity.getRole(),
                entity.getFailedLoginAttempts(),
                entity.getPasswordChangedAt(),
                entity.getLastLoginAt()
        );
    }

    public static UserEntity toEntity(User u) {
        return new UserEntity(
                u.getId(),
                u.getUsername(),
                u.getPasswordHash(),
                u.getMatricula(),
                u.getRole(),
                u.isActive(),
                u.isLocked(),
                u.getFailedLoginAttempts(),
                u.getPasswordChangedAt(),
                u.getLastLoginAt(),
                u.getCreatedAt()
        );
    }
}

