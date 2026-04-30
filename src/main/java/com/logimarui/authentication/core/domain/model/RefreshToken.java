package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    private Long id;
    private Session session;
    private String tokenHash;
    private Instant createdAt;
    private Instant expiresAt;
    private RefreshTokenStatus refreshTokenStatus;

    private RefreshToken(
            Session session,
            String tokenHash,
            Duration ttl
    ) {
        Instant now = Instant.now();

        this.session = session;
        this.tokenHash = tokenHash;
        this.createdAt = now;
        this.expiresAt = now.plus(ttl);
        this.refreshTokenStatus = RefreshTokenStatus.ACTIVE;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull RefreshToken create(
            Session session,
            String tokenHash,
            Duration ttl
    ) {
        if (session == null) throw new IllegalArgumentException("session is required");
        if (tokenHash == null || tokenHash.isBlank()) throw new IllegalArgumentException("tokenHash is required");
        if (ttl == null || ttl.isZero() || ttl.isNegative()) throw new IllegalArgumentException("invalid ttl");

        return new RefreshToken(
                session,
                tokenHash,
                ttl
        );
    }

    public static @NotNull RefreshToken reconstitute(
            Long id,
            Session session,
            String tokenHash,
            Instant createdAt,
            @NotNull Instant expiresAt,
            RefreshTokenStatus refreshTokenStatus
    ) {
        if (session == null) throw new IllegalArgumentException("session is required");
        if (tokenHash == null || tokenHash.isBlank()) throw new IllegalArgumentException("tokenHash is required");
        if (createdAt == null) throw new IllegalArgumentException("createdAt is required");
        if (expiresAt == null) throw new IllegalArgumentException("expiresAt is required");
        if (refreshTokenStatus == null) throw new IllegalArgumentException("refreshTokenStatus is required");

        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("RefreshToken expiration must be after creation");
        }

        RefreshToken token = new RefreshToken();
        token.id = id;
        token.session = session;
        token.tokenHash = tokenHash;
        token.createdAt = createdAt;
        token.expiresAt = expiresAt;
        token.refreshTokenStatus = refreshTokenStatus;

        return token;
    }


    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }
    public boolean isActive(Instant now) {
        return refreshTokenStatus == RefreshTokenStatus.ACTIVE && !isExpired(now);
    }
    public boolean isValid(Instant now) {
        return isActive(now) && session.isValid(now);
    }
    public boolean isRevoked() {
        return refreshTokenStatus == RefreshTokenStatus.REVOKED;
    }
    public void revoke() {
        this.refreshTokenStatus = RefreshTokenStatus.REVOKED;
    }
    public void rotate(String newTokenHash, Duration ttl, Instant now) {
        if (now == null) throw new IllegalArgumentException("now is required");
        if (newTokenHash == null || newTokenHash.isBlank()) throw new IllegalArgumentException("newTokenHash is required");
        if (ttl == null || ttl.isZero() || ttl.isNegative()) throw new IllegalArgumentException("invalid ttl");
        if (!isValid(now)) throw new IllegalStateException("Cannot rotate invalid refresh token");

        Instant newExpiresAt = now.plus(ttl);
        if (!newExpiresAt.isAfter(now)) {
            throw new IllegalStateException("expiresAt must be after now");
        }

        this.tokenHash = newTokenHash;
        this.expiresAt = newExpiresAt;
    }
}
