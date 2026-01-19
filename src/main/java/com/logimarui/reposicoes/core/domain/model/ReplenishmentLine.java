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
    private Long replenishmentId;
    private Long productId;
    private ReplenishmentReason replenishmentReason;
    private UnitOfMeansure unitOfMeansure;
    private int quantity;
    private DamageRecord damageRecord;

}
