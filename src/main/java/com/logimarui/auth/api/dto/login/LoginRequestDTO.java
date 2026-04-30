package com.logimarui.auth.api.dto.login;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record LoginRequestDTO(
        @CPF String cpf,
        @NotBlank String password
){}
