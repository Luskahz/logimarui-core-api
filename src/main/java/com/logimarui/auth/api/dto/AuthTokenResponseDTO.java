package com.logimarui.auth.api.dto;

public record AuthTokenResponseDTO(
   String refreshToken,
   String accessToken,
   long expiresInSeconds
) {}
