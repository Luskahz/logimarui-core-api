package com.logimarui.authentication.api.dto.forgotPassword;

import org.hibernate.validator.constraints.br.CPF;

public record ForgotPasswordRequestDTO(
        @CPF String cpf
){

}

