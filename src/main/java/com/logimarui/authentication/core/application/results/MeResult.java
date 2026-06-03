package com.logimarui.authentication.core.application.results;

public record MeResult(
        Long userId,
        Long sessionId,
        boolean sessionActive,
        long expiresInSeconds
) {
}