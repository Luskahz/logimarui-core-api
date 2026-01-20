package com.logimarui.auth.core.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Session {

    private Long id;
    private Long userId;
    private boolean active;
    private String device;
    private String lastIpAddress;
    private Instant createdAt;
    private Instant expiresAt;

    private Session(Long userId, String ip, String device) {
        this.userId = userId;
        this.lastIpAddress = ip;
        this.device = device;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(Duration.ofDays(7));
        this.active = true;
    }

    public static Session create(Long userId, String ip, String device) {
        return new Session(userId, ip, device);
    }

    public static Session reconstitute(
            Long id,
            Long userId,
            String device,
            String lastIpAddress,
            Instant createdAt,
            Instant expiresAt,
            boolean active
    ) {
        if (expiresAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Session expiration must be after creation");
        }

        Session session = new Session();
        session.id = id;
        session.userId = userId;
        session.device = device;
        session.lastIpAddress = lastIpAddress;
        session.createdAt = createdAt;
        session.expiresAt = expiresAt;
        session.active = active;

        return session;
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
