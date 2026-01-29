package com.logimarui.infra.security.jwt;

import java.time.Instant;
import java.util.List;

public record JwtAuthenticatedUser(
        Long userId,
        Long sessionId,
        List<String> roles,
        Instant expiresAt
) {}