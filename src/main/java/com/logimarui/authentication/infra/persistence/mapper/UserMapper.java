package com.logimarui.authentication.infra.persistence.mapper;

import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.infra.persistence.entity.UserEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getCpf(),
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
                u.getCpf(),
                u.getUsername(),
                u.getPasswordHash(),
                EnumSet.copyOf(u.getRoles()),
                u.getUserStatus(),
                u.getCreatedAt(),
                u.getLastLoginAt(),
                u.getPasswordChangedAt(),
                u.getFailedLoginAttempts()
        );
    }
}

