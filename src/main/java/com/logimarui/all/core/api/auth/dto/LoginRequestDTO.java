package com.logimarui.all.core.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        String usuario,

        @NotBlank
        String senha
) {}
