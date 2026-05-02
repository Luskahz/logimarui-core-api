package com.logimarui.authorization.api.controller;

import com.logimarui.authorization.api.dto.PermissionResponseDTO;
import com.logimarui.authorization.core.application.result.FindAllPermissionsResult;
import com.logimarui.authorization.core.application.service.PermissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authorization/permissions")
@RequiredArgsConstructor
@Validated
public class PermissionController {
    PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionResponseDTO>> findAllPermissions() {
        FindAllPermissionsResult result = permissionService.findAllPermissions();




return null;

    }

    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionResponseDTO> findPermissionById(
            @PathVariable @Positive Long permissionId
    ) {
        return null;
    }

}
