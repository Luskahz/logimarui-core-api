package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.Role;
import lombok.*;

import java.time.Instant;

@Getter
public class User {

    private final Long id;
    private final String username;
    private final Instant createdAt;

    private String passwordHash;
    private boolean active;
    private boolean locked;

    private Role role;
    private int failedLoginAttempts;

    private Instant passwordChangedAt;
    private Instant lastLoginAt;

    public User(
            Long id,
            String username,
            String passwordHash,
            Role role,
            Instant createdAt
    ) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;

        this.active = true;
        this.locked = false;
        this.failedLoginAttempts = 0;
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

