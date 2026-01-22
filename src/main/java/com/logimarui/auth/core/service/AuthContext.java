package com.logimarui.auth.core.service;

import java.util.List;

public record AuthContext(
        Long userId,
        List<String> roles,
        Long sessionId,
        boolean sessionActive,
        long expiresInSeconds
) {
}
