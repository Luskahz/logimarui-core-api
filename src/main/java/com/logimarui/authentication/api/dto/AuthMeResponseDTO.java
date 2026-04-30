package com.logimarui.authentication.api.dto;

import java.util.List;

public record AuthMeResponseDTO(
        Long userId,
        List<String> roles,
        Long sessionId,
        boolean sessionActive,
        long expiresInSeconds
) {}
