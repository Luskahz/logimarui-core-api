package com.logimarui.authentication.core.application.results;

public record AuthContext(
        Long userId,
        Long sessionId,
        boolean sessionActive,
        long expiresInSeconds
) {
}
