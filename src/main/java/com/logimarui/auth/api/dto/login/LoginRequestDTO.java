package com.logimarui.auth.api.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull Long employeeId,
        @NotBlank String password
){}
