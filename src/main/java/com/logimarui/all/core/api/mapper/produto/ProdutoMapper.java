package com.logimarui.all.core.api.mapper.produto;

import com.logimarui.all.core.api.domain.read.Produto;
import com.logimarui.all.core.api.dto.produto.ProdutoResponseDTO;

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
