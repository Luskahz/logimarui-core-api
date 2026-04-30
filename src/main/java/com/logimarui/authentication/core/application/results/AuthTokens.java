package com.logimarui.authentication.core.application.results;


public record AuthTokens(
        String refreshToken,
        String accessToken,
        long expiresInSeconds
) {}