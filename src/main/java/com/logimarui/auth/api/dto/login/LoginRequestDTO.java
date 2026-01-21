package com.logimarui.auth.api.dto.login;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank Long matricula,
        @NotBlank String password
){}
