package com.logimarui.auth.core.domain.model;

import java.time.Instant;

public record IssuedAccessToken(
        String token,
        Instant expiresAt
) {}
