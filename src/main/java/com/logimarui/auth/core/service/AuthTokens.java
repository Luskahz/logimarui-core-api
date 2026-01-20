package com.logimarui.auth.core.service;

public record AuthTokens(
        String refreshToken,
        String accessToken,
        long expiresIn
) {}