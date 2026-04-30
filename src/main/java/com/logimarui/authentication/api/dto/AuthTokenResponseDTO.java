package com.logimarui.authentication.api.dto;

public record AuthTokenResponseDTO(
   String refreshToken,
   String accessToken,
   long expiresInSeconds
) {}
