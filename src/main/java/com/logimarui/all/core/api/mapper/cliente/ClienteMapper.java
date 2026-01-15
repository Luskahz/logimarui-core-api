package com.logimarui.all.core.api.mapper.cliente;

import com.logimarui.all.core.api.domain.read.Cliente;
import com.logimarui.all.core.api.dto.cliente.ClienteResponseDTO;

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
