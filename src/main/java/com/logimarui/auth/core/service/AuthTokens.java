package com.logimarui.auth.core.service;

import java.time.Instant;

public record AuthTokens(
        String refreshToken,
        String accessToken,
        Instant expiresIn
) {}