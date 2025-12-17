package com.logimarui.core.api.domain.read.pedido;

import com.logimarui.core.api.domain.read.Cliente;
import com.logimarui.core.api.domain.read.Mapa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private Long notaFiscal;
    private Integer serie;
    private Cliente cliente;
    private Mapa mapa;
    private LocalDate dataEntrega;
    private String status;
    private List<ItemPedido> itens;

}
