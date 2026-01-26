package com.logimarui.auth.core.service;

public record AuthContext(
        Long userId,
        Long sessionId,
        boolean sessionActive,
        long expiresInSeconds
) {
}
