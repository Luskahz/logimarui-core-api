package com.logimarui.reposicoes.core.domain.model;

import com.logimarui.reposicoes.core.domain.enums.UnitOfMeansure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long codigoProduto;
    private String nomeProduto;
    private UnitOfMeansure unidade;
    private Long maxUnidadesPPack;
}
