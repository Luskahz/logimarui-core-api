package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.SessionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

import static com.logimarui.auth.core.domain.enums.SessionStatus.LOGGED_OUT;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Session {

    private Long id;
    private Long userId;
    private String deviceId;
    private String lastIpAddress;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant loggedOutAt;
    private SessionStatus sessionStatus;

    private Session(
            Long userId,
            String ip,
            String deviceId,
            Duration ttl
    ) {


        this.userId = userId;
        this.deviceId = deviceId;
        this.lastIpAddress = ip;
        Instant now = Instant.now();
        this.createdAt = now;
        this.expiresAt = now.plus(ttl) ;
        this.sessionStatus = SessionStatus.ACTIVE;
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull Session create(
            Long userId,
            String ip,
            String deviceId,
            Duration ttl
    ) {
        if (ttl == null || ttl.isZero() || ttl.isNegative())
            throw new IllegalArgumentException("invalid ttl");
        return new Session(
                userId,
                ip,
                deviceId,
                ttl
        );
    }

    public static @NotNull Session reconstitute(
            Long id,
            Long userId,
            String deviceId,
            String lastIpAddress,
            Instant createdAt,
            @NotNull Instant expiresAt,
            Instant loggedOutAt,
            SessionStatus sessionStatus
    ) {
        if (createdAt == null) throw new IllegalArgumentException("createdAt is required");
        if (expiresAt == null) throw new IllegalArgumentException("expiresAt is required");
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("expiresAt must be after createdAt");
        }
        if (sessionStatus == null) throw new IllegalArgumentException("sessionStatus is required");

        switch (sessionStatus) {
            case ACTIVE -> {
                if (loggedOutAt != null) throw new IllegalStateException("ACTIVE cannot have loggedOutAt");
            }
            case LOGGED_OUT -> {
                if (loggedOutAt == null) throw new IllegalStateException("LOGGED_OUT must have loggedOutAt");
                if (loggedOutAt.isBefore(createdAt)) throw new IllegalStateException("loggedOutAt cannot be before createdAt");
            }
            case REVOKED -> {//?
                if (loggedOutAt != null) throw new IllegalStateException("REVOKED cannot have loggedOutAt");
            }
        }

        Session session = new Session();
        session.id = id;
        session.userId = userId;
        session.deviceId = deviceId;
        session.lastIpAddress = lastIpAddress;
        session.createdAt = createdAt;
        session.expiresAt = expiresAt;
        session.loggedOutAt = loggedOutAt;
        session.sessionStatus = sessionStatus;
        return session;
    }


    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }
    public boolean isActive(Instant now) {
        return sessionStatus == SessionStatus.ACTIVE && !isExpired(now);
    }
    public boolean isRevoked() {
        return sessionStatus == SessionStatus.REVOKED;
    }
    public boolean isLoggedOut() {
        return sessionStatus == LOGGED_OUT;
    }
    public boolean isValid(Instant now) {
        return !isExpired(now) && !isRevoked() && !isLoggedOut();
    }

    public void updateIpAddress(String ipAddress, Instant now) {
        if (!isValid(now)) {
            throw new IllegalStateException("Cannot update IP of inactive session");
        }
        this.lastIpAddress = ipAddress;
    }
    public void revoke() {
        if (sessionStatus != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Only active sessions can be revoked");
        }
        this.sessionStatus = SessionStatus.REVOKED;
    }
    public void logout(Instant now) {
        if (this.sessionStatus != SessionStatus.ACTIVE) {
            return;
        }
        this.sessionStatus = LOGGED_OUT;
        this.loggedOutAt = now;
    }
}
