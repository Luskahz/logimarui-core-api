package com.logimarui.all.core.api.controller.read;

import com.logimarui.all.core.api.dto.supervisor.SupervisorResponseDTO;
import com.logimarui.all.core.api.mapper.supervisor.SupervisorMapper;
import com.logimarui.all.core.api.service.SupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/supervisores/")
public class SupervisorController {
    private final SupervisorService supervisorService;

    @GetMapping("/{codigo}")
    public SupervisorResponseDTO buscar(
            @PathVariable Long codigo,
            Authentication authentication
        ){
        String usuario = authentication.getName();
        return new SupervisorMapper().toResponse(supervisorService.buscar(codigo));

    }
}
