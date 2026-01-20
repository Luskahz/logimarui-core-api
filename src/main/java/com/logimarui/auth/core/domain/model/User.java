package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private Long id;
    private String username;
    private Instant createdAt;
    private Long matricula;
    private String passwordHash;
    private boolean active;
    private boolean locked;
    private Role role;
    private int failedLoginAttempts;
    private Instant passwordChangedAt;
    private Instant lastLoginAt;

    private User(String username, String passwordHash, Long matricula) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.matricula = matricula;

        this.createdAt = Instant.now();
        this.active = true;
        this.locked = false;
        this.role = Role.INDEFINIDO;
        this.failedLoginAttempts = 0;
        this.lastLoginAt = Instant.now();
    }

    public static User create(String username, String passwordHash, Long matricula) {
        return new User(username, passwordHash, matricula);
    }


    public static User reconstitute(
            Long id,
            String username,
            Instant createdAt,
            Long matricula,
            String passwordHash,
            boolean active,
            boolean locked,
            Role role,
            int failedLoginAttempts,
            Instant passwordChangedAt,
            Instant lastLoginAt
    ) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.createdAt = createdAt;
        user.matricula = matricula;
        user.passwordHash = passwordHash;
        user.active = active;
        user.locked = locked;
        user.role = role;
        user.failedLoginAttempts = failedLoginAttempts;
        user.passwordChangedAt = passwordChangedAt;
        user.lastLoginAt = lastLoginAt;

        return user;
    }


    public void registerFailedLogin() {
        failedLoginAttempts++;
        if (failedLoginAttempts >= 5) {
            locked = true;
        }
    }
    public void changePassword(String newHash, Instant now) {
        this.passwordHash = newHash;
        this.passwordChangedAt = now;
        this.failedLoginAttempts = 0;
        this.locked = false;
    }
    public boolean isBlocked() {
        return !active || locked;
    }
    public void deactivate() {
        this.active = false;
    }
    public void recordSuccessfulLogin(Instant now) {
        this.lastLoginAt = now;
        this.failedLoginAttempts = 0;
    }
}
