package com.logimarui.core.api.mapper.produto;

import com.logimarui.core.api.domain.read.Produto;
import com.logimarui.core.api.dto.produto.ProdutoResponseDTO;

public class ProdutoMapper {
    public static ProdutoResponseDTO toResponse(Produto produto){
        return new ProdutoResponseDTO(
                produto.getCodigo(),
                produto.getDescricao(),
                produto.getEmbalagem(),
                produto.getPeso()
        );
    }
}
