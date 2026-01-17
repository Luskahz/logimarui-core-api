package com.logimarui.reposicoes.core.domain.model;

import com.logimarui.reposicoes.core.domain.enums.ReplenishmentReason;
import com.logimarui.reposicoes.core.domain.enums.UnitOfMeansure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplenishmentLine {
    private Long codigoProduto;
    private ReplenishmentReason motivoReposicao;
    private UnitOfMeansure unidade;
    private int quantidade;
    private DamageRecord imagemAvaria;

}
