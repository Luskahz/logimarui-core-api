package com.logimarui.all.core.api.controller.read;

import com.logimarui.all.core.api.dto.cliente.ClienteResponseDTO;
import com.logimarui.all.core.api.mapper.cliente.ClienteMapper;
import com.logimarui.all.core.api.service.ClienteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes/")
@AllArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;


    @GetMapping("/{codigo}")
    public ClienteResponseDTO buscar(
            @PathVariable Long codigo,
            Authentication authentication
        ){
        String usuario = authentication.getName();
        return new ClienteMapper().toResponse(clienteService.buscar(codigo));

    }


}
