package com.logimarui.auth.api.dto;

import java.time.Instant;
import java.util.List;

public record AuthMeResponseDTO(
        Long userId,
        List<String> roles,
        Long sessionId,
        boolean sessionActive,
        long expiresInSeconds
) {}
