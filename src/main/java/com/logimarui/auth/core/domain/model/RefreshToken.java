package com.logimarui.auth.core.domain.model;

import lombok.Getter;

import java.time.Instant;

@Getter

public class RefreshToken {

    private final Session session;

    private final String tokenHash;

    private final Instant createdAt;
    private final Instant expiresAt;

    private boolean revoked;

    public RefreshToken(
            Session session,
            String tokenHash,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.session = session;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.revoked = false;
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

