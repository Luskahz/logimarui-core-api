package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WMS {
    private Mapa mapa;
    private LocalDate dataEntrega;
    private Produto produto;
    private String descricaoBaia;
    private Integer quantidade;
    private Integer sequenciaCarregamento;
    private String usuarioSeparador;
}
