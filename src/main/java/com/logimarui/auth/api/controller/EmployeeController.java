package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.response.EmployeeNameResponseDTO;
import com.logimarui.auth.core.application.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
@Tag(
        name = "Authentication - Employees",
        description = "Endpoints auxiliares de autenticação relacionados a colaboradores"
)
public class EmployeeController {

    private final AuthService authService;

    @GetMapping("/employees/{employeeId}/name")
    @Operation(
            summary = "Buscar nome do colaborador",
            description = "Retorna o nome do colaborador a partir da matrícula informada."
    )
    public EmployeeNameResponseDTO employeeName(
            @Parameter(
                    description = "Matrícula do colaborador",
                    example = "123",
                    required = true
            )
            @PathVariable Long employeeId
    ) {
        String name = authService.nameFromEmployee(employeeId);

        return new EmployeeNameResponseDTO(employeeId, name);
    }
}