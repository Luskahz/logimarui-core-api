package com.logimarui.auth.api.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RegisterRequestDTO(
        @NotBlank String username,
        @NotBlank String password,
        @Positive Long matricula //vou usar isso só no service pra definir o role via ADMView
) {
}
