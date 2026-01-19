package com.logimarui.auth.core.domain.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Session {

    private final Long id;

    private final Long userId;

    private boolean active;

    private final String device;
    private String lastIpAddress;

    private final Instant createdAt;
    private final Instant expiresAt;

    public Session(
            Long id,
            Long userId,
            String device,
            String lastIpAddress,
            Instant createdAt,
            Instant expiresAt
    ) {
        if (expiresAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Session expiration must be after creation");
        }
        this.id = id;
        this.userId = userId;
        this.device = device;
        this.lastIpAddress = lastIpAddress;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = true;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }

    public boolean isValid(Instant now) {
        return active && !isExpired(now);
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateIpAddress(String ipAddress) {
        this.lastIpAddress = ipAddress;
    }
}
