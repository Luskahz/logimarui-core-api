package com.logimarui.occurrence.core.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSummary(
        Long orderNumber,
        Long invoiceNumber,
        LocalDate deliveryDate,
        LocalDate invoiceIssueDate,
        BigDecimal orderValue,
        BigDecimal totalHectoliters
) {
}
