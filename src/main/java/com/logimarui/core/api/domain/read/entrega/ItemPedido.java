package com.logimarui.core.api.domain.read.entrega;

import com.logimarui.core.api.domain.read.Produto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedido {
    private Produto produto;
    private int quantidade;
    private BigDecimal volume;
    private BigDecimal peso;
    private BigDecimal totalValor;
}
