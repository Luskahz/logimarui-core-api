package com.logimarui.core.api.controller.read;

import com.logimarui.core.api.dto.motorista.MotoristaResponseDTO;
import com.logimarui.core.api.mapper.motorista.MotoristaMapper;
import com.logimarui.core.api.service.MotoristaService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/motoristas/")
public class MotoristaController {
    private final MotoristaService motoristaService;

    @GetMapping("/{codigo}")
    public MotoristaResponseDTO buscar(
            @PathVariable Long codigo,
            Authentication authentication
    ){
        String usuario = authentication.getName();
        return new MotoristaMapper().toResponse(motoristaService.buscar(codigo));
    }


}
