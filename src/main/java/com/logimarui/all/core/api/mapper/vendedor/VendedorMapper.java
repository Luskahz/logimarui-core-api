package com.logimarui.all.core.api.mapper.vendedor;

import com.logimarui.all.core.api.domain.read.Vendedor;
import com.logimarui.all.core.api.dto.vendedor.VendedorResponseDTO;

public class VendedorMapper {

    public static VendedorResponseDTO toResponse(Vendedor vendedor){
        return new VendedorResponseDTO(
                vendedor.getCodigo(),
                vendedor.getNome(),
                vendedor.getArea()
        );
    }
}
