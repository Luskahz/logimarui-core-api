package com.logimarui.all.core.api.dto.produto;

import java.math.BigDecimal;

public record ProdutoFiltroDTO (
    String descricao,
    String embalagem,
    BigDecimal peso
){}
