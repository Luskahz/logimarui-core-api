package com.logimarui.auth.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com o nome do colaborador")
public record EmployeeNameResponseDTO(

        @Schema(
                description = "Matrícula do colaborador",
                example = "123"
        )
        Long employeeId,

        @Schema(
                description = "Nome do colaborador",
                example = "João da Silva"
        )
        String name
) {
}