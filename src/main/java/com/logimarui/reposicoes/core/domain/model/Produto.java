package com.logimarui.reposicoes.core.domain.model;

import com.logimarui.reposicoes.core.domain.enums.UnidadeQuantidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
    private Long codigoProduto;
    private String nomeProduto;
    private UnidadeQuantidade unidade;
    private Long maxUnidadesPPack;
}
