package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.SessionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Session {

    private Long id;
    private Long userId;
    private String deviceId;
    private String lastIpAddress;
    private Instant createdAt;
    private Instant expiresAt;
    private SessionStatus sessionStatus;

    private Session(Long userId, String ip, String deviceId) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.lastIpAddress = ip;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(Duration.ofDays(7));
        this.sessionStatus = SessionStatus.ACTIVE;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull Session create(Long userId, String ip, String deviceId) {
        return new Session(userId, ip, deviceId);
    }

    public static @NotNull Session reconstitute(
            Long id,
            Long userId,
            String deviceId,
            String lastIpAddress,
            Instant createdAt,
            @NotNull Instant expiresAt,
            SessionStatus sessionStatus
    ) {
        if (expiresAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Session expiration must be after creation");
        }

        Session session = new Session();
        session.id = id;
        session.userId = userId;
        session.deviceId = deviceId;
        session.lastIpAddress = lastIpAddress;
        session.createdAt = createdAt;
        session.expiresAt = expiresAt;
        session.sessionStatus = sessionStatus;

        return session;
    }


    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }
    public boolean isActive(Instant now) {
        return sessionStatus == SessionStatus.ACTIVE && !isExpired(now);
    }
    public boolean isRevoked() {
        return sessionStatus == SessionStatus.REVOKED;
    }
    public boolean isLoggedOut() {
        return sessionStatus == SessionStatus.LOGGED_OUT;
    }
    public boolean isInvalid(Instant now) {
        return isExpired(now) || isRevoked() || isLoggedOut() || isExpired(now);
    }
    public void updateIpAddress(String ipAddress) {
        if (sessionStatus != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot update IP of inactive session");
        }
        this.lastIpAddress = ipAddress;
    }
    public void revoke() {
        this.sessionStatus = SessionStatus.REVOKED;
    }
    public void logout() {
        this.sessionStatus = SessionStatus.LOGGED_OUT;
    }
}
