package com.logimarui.core.api.domain.read.entrega;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private Long notaFiscal;
    private Long codigoVendedor;
    private int serie;
    private Long codigoCliente;
    private LocalDate dataEntrega;
    private String status;
    private Double totalValor;
    private BigDecimal totalHl;
    private BigDecimal totalPeso;
    private List<ItemPedido> itens;
}
