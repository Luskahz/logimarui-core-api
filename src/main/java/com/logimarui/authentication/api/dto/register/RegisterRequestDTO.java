package com.logimarui.auth.api.dto.register;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "Dados necessários para cadastro de um novo usuário")
public record RegisterRequestDTO(

        @Schema(
                description = "Nome de usuário usado para login",
                example = "lucas.silva",
                minLength = 3,
                maxLength = 255,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "O username é obrigatório")
        @Size(min = 3, max = 255, message = "O username deve ter entre 3 e 255 caracteres")
        String username,

        @Schema(
                description = "Senha do usuário",
                example = "Senha@123",
                minLength = 8,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 100, message = "A senha deve ter no mínimo 8 caracteres")
        String password,

        @Schema(
                description = "CPF do usuário, usado para identificação e definição de permissões internas",
                example = "12345678909",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos")
        String cpf
) {
}