package com.logimarui.occurrence.api.v1.dto;

import java.math.BigDecimal;

public record InvoiceItemResponse(
        Long productCode,
        String productName,
        BigDecimal quantity
) {
}
