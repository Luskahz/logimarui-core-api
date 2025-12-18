package com.logimarui.core.api.dto.produto;

import java.math.BigDecimal;

public record ProdutoResponseDTO (
        Long codigo,
        String descricao,
        String embalagem,
        BigDecimal peso
){}
