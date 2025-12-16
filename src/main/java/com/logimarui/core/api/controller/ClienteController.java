package com.logimarui.core.api.controller;

import com.logimarui.core.api.domain.read.Cliente;
import com.logimarui.core.api.dto.cliente.ClienteResponseDTO;
import com.logimarui.core.api.mapper.cliente.ClienteMapper;
import com.logimarui.core.api.service.ClienteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes/")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    @GetMapping("/{codigo}")
    public ClienteResponseDTO buscar(
            @PathVariable Long codigo,
            Authentication authentication
        ){
        String usuario = authentication.getName();
        return new ClienteMapper().toResponse(clienteService.buscar(codigo));

    }


}
