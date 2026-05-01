package com.logimarui.authorization.api.controller;


import com.logimarui.authorization.api.dto.CreateRoleRequestDTO;
import com.logimarui.authorization.api.dto.RoleDetailsResponseDTO;
import com.logimarui.authorization.api.dto.UpdateRoleRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorization/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

    @Operation(
            summary = "Criar role",
            description = "Cria uma nova role de autorização, opcionalmente já vinculada a um conjunto de permissões."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Role já cadastrada")
    })
    @PostMapping
    public ResponseEntity<RoleDetailsResponseDTO> createRole(
            @RequestBody @Valid CreateRoleRequestDTO request
    ) {
        return null;
    }

    @GetMapping
    public ResponseEntity<List<RoleDetailsResponseDTO>> findAllRoles() {
        return null;
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDetailsResponseDTO> findRoleById(
            @PathVariable @Positive Long roleId
    ) {
        return null;
    }

    @PatchMapping("/{roleId}")
    public ResponseEntity<RoleDetailsResponseDTO> updateRole(
            @PathVariable @Positive Long roleId,
            @RequestBody @Valid UpdateRoleRequestDTO request
    ) {
        return null;
    }

    @PatchMapping("/{roleId}/disable")
    public ResponseEntity<Void> disableRole(
            @PathVariable @Positive Long roleId
    ) {
        return null;
    }

    @PatchMapping("/{roleId}/enable")
    public ResponseEntity<Void> enableRole(
            @PathVariable @Positive Long roleId
    ) {
        return null;
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable @Positive Long roleId
    ) {
        return null;
    }
}
