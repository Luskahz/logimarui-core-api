package com.logimarui.auth.core.application.results;

public record AuthContext(
        Long userId,
        Long sessionId,
        boolean sessionActive,
        long expiresInSeconds
) {
}
