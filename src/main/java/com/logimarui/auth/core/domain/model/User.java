package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.Role;
import com.logimarui.auth.core.domain.enums.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private Long id;
    private Long matricula;
    private String username;
    private String passwordHash;
    private Role role;
    private UserStatus userStatus;
    private Instant createdAt;
    private Instant lastLoginAt;
    private Instant passwordChangedAt;
    private int failedLoginAttempts;

    private User(String username, String passwordHash, Long matricula) {
        this.matricula = matricula;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = Role.INDEFINIDO;
        this.userStatus = UserStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.lastLoginAt = Instant.now();
        this.failedLoginAttempts = 0;
    }

    public static User create(String username, String passwordHash, Long matricula) {
        return new User(username, passwordHash, matricula);
    }


    public static User reconstitute(
            Long id,
            Long matricula,
            String username,
            String passwordHash,
            Role role,
            UserStatus userStatus,
            Instant createdAt,
            Instant lastLoginAt,
            Instant passwordChangedAt,
            int failedLoginAttempts
    ) {
        User user = new User();
        user.id = id;
        user.matricula = matricula;
        user.username = username;
        user.passwordHash = passwordHash;
        user.role = role;
        user.userStatus = userStatus;
        user.createdAt = createdAt;
        user.lastLoginAt = lastLoginAt;
        user.passwordChangedAt = passwordChangedAt;
        user.failedLoginAttempts = failedLoginAttempts;
        return user;
    }


    public boolean canAuthenticate() {
        return userStatus == UserStatus.ACTIVE;
    }
    public boolean isLocked() {
        return userStatus == UserStatus.LOCKED;
    }

    public boolean isInactive() {
        return userStatus == UserStatus.INACTIVE;
    }

    public void registerFailedLogin() {
        failedLoginAttempts++;

        if (failedLoginAttempts >= 5) {
            userStatus = UserStatus.LOCKED;
        }
    }
    public void recordSuccessfulLogin(Instant now) {
        this.lastLoginAt = now;
        this.failedLoginAttempts = 0;
    }

    public void changePassword(String newHash, Instant now) {
        this.passwordHash = newHash;
        this.passwordChangedAt = now;
        this.failedLoginAttempts = 0;
        this.userStatus = UserStatus.ACTIVE;
    }
    public void deactivate() {
        this.userStatus = UserStatus.INACTIVE;
    }
    public boolean isBlocked() {
        return isLocked() || isInactive();
    }
}
