package com.logimarui.authentication.api.controller;

import com.logimarui.authentication.api.dto.EmployeeNameResponseDTO;
import com.logimarui.authentication.core.application.services.EmployeeService;
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

    private final EmployeeService employeeService;

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
        String name = employeeService.nameFromEmployee(employeeId);

        return new EmployeeNameResponseDTO(employeeId, name);
    }
}