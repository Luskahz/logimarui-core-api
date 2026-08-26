package com.logimarui.occurrence.core.domain.model;

import java.math.BigDecimal;

public record ReturnAlertContext(
        Long customerId,
        Long invoiceNumber,
        BigDecimal orderValue,
        BigDecimal totalHectoliters
) {
}
