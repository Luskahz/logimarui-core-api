package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WMS {
    private Long mapa;
    private LocalDate dataEntrega;
    private Long produto;
    private String descricaoProduto;
    private String descricaoBaia;
    private String tipoProduto;
    private Integer quantidade;
    private Integer sequenciaCarregamento;
    private String usuarioSeparador;
}
