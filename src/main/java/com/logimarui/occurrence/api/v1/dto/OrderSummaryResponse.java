package com.logimarui.occurrence.api.v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSummaryResponse(
        Long orderNumber,
        Long invoiceNumber,
        LocalDate deliveryDate,
        LocalDate invoiceIssueDate,
        BigDecimal orderValue,
        BigDecimal totalHectoliters
) {
}
