package com.logimarui.core.api.mapper.vendedor;

import com.logimarui.core.api.domain.read.Vendedor;
import com.logimarui.core.api.dto.vendedor.VendedorResponseDTO;

public class VendedorMapper {

    public static VendedorResponseDTO toResponse(Vendedor vendedor){
        return new VendedorResponseDTO(
                vendedor.getCodigo(),
                vendedor.getNome(),
                vendedor.getArea()
        );
    }
}
