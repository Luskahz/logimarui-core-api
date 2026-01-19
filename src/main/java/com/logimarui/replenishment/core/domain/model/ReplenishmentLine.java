package com.logimarui.replenishment.core.domain.model;

import com.logimarui.replenishment.core.domain.enums.ReplenishmentReason;
import com.logimarui.replenishment.core.domain.enums.UnitOfMeansure;
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
