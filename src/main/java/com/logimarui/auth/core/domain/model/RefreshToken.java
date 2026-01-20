package com.logimarui.auth.core.domain.model;

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
    private boolean revoked;

    private RefreshToken(Session session, String tokenHash) {
        this.session = session;
        this.tokenHash = tokenHash;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(Duration.ofDays(30));
        this.revoked = false;
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
            boolean revoked
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
        token.revoked = revoked;

        return token;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }
    public boolean isValid(Instant now) {
        return !revoked && !isExpired(now) && session.isValid(now);
    }
    public void revoke() {
        this.revoked = true;
    }
}
