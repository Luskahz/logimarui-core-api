package com.logimarui.reposicoes.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Veiculo {
    private String placaVeiculo;
    private Long codigoVeiculo;
}
