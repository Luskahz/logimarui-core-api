package com.logimarui.auth.api.dto;

import java.time.Instant;

public record AuthTokenResponseDTO(
   String refreshToken,
   String accessToken,
   long accessTokenExpiresIn
) {}
