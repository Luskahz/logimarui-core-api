package com.logimarui.auth.infra.persistence.mapper;

import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.infra.persistence.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getCreatedAt()
        );
    }

    public static UserEntity toEntity(User u) {
        return new UserEntity(
                u.getId(),
                u.getUsername(),
                u.getPasswordHash(),
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

