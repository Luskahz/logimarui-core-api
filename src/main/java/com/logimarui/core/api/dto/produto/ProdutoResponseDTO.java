package com.logimarui.core.api.dto.produto;

public record ProdutoResponseDTO (
        Long codigo,
        String descricao,
        String embalagem,
        Float peso
){}
