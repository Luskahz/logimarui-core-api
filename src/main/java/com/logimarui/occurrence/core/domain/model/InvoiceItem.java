package com.logimarui.occurrence.core.domain.model;

import java.math.BigDecimal;

public record InvoiceItem(
        Long productCode,
        String productName,
        BigDecimal quantity
) {
}
