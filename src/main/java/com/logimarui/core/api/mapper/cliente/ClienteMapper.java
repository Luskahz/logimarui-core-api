package com.logimarui.core.api.mapper.cliente;

import com.logimarui.core.api.domain.read.Cliente;
import com.logimarui.core.api.dto.cliente.ClienteResponseDTO;

public class ClienteMapper {
    public static ClienteResponseDTO toResponse(Cliente cliente){
        return new ClienteResponseDTO(
                cliente.getCodigo(),
                cliente.getFantasia(),
                cliente.getCpf(),
                cliente.getCnpj(),
                cliente.getRazaoSocial(),
                cliente.getLatitude(),
                cliente.getLongitude()
        );
    }

}
