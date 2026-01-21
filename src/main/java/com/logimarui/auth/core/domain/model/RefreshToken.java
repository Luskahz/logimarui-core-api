package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

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

    private RefreshToken(Session session, String tokenHash) {
        this.session = session;
        this.tokenHash = tokenHash;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(Duration.ofDays(30));
        this.refreshTokenStatus = RefreshTokenStatus.ACTIVE;
    }

    public static RefreshToken create(Session session, String tokenHash) {
        return new RefreshToken(session, tokenHash);
    }

    public static RefreshToken reconstitute(
            Long id,
            Session session,
            String tokenHash,
            Instant createdAt,
            Instant expiresAt,
            RefreshTokenStatus refreshTokenStatus
    ) {
        if (expiresAt.isBefore(createdAt)) {
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
        return expiresAt.isBefore(now);
    }
    public boolean isActive(Instant now) {
        return refreshTokenStatus == RefreshTokenStatus.ACTIVE && !isExpired(now);
    }
    public boolean isRevoked() {
        return refreshTokenStatus == RefreshTokenStatus.REVOKED;
    }
    public boolean isValid(Instant now) {
        return isActive(now);
    }
    public void revoke() {
        this.refreshTokenStatus = RefreshTokenStatus.REVOKED;
    }
}
