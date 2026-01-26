package com.logimarui.auth.infra.persistence.mapper;

import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.infra.persistence.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getRoles(),
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
                u.getEmployeeId(),
                u.getUsername(),
                u.getPasswordHash(),
                Set.copyOf(u.getRoles()),
                u.getUserStatus(),
                u.getCreatedAt(),
                u.getLastLoginAt(),
                u.getPasswordChangedAt(),
                u.getFailedLoginAttempts()
        );
    }
}

