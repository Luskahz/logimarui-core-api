package com.logimarui.occurrence.api.v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSummaryResponse(
        Long orderNumber,
        Long invoiceNumber,
        Long customerId,
        String customerName,
        String tradeName,
        LocalDate deliveryDate,
        LocalDate invoiceIssueDate,
        BigDecimal orderValue,
        BigDecimal totalHectoliters,
        BigDecimal totalWeightKg,
        Long routeNumber,
        Long sectorCode,
        String driverName,
        String orderType,
        String externalStatus
) {
}
