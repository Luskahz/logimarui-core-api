package com.logimarui.auth.core.application.results;


public record AuthTokens(
        String refreshToken,
        String accessToken,
        long expiresInSeconds
) {}