package com.logimarui.auth.api.dto.forgotPassword;

import org.hibernate.validator.constraints.br.CPF;

public record ForgotPasswordRequestDTO(
        @CPF String cpf
){

}

