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
                entity.getMatricula(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getUserStatus(),
                entity.getCreatedAt(),
                entity.getLastLoginAt(),
                entity.getPasswordChangedAt(),
                entity.getFailedLoginAttempts()
        );
    }

    public static UserEntity toEntity(User u) {
        return new UserEntity(
                u.getId(),
                u.getMatricula(),
                u.getUsername(),
                u.getPasswordHash(),
                u.getRole(),
                u.getUserStatus(),
                u.getCreatedAt(),
                u.getLastLoginAt(),
                u.getPasswordChangedAt(),
                u.getFailedLoginAttempts()
        );
    }
}

