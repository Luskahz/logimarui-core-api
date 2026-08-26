package com.logimarui.occurrence.api.v1.dto;

import java.math.BigDecimal;

public record ReturnAlertContextResponse(
        Long customerId,
        Long invoiceNumber,
        BigDecimal orderValue,
        BigDecimal totalHectoliters
) {
}
