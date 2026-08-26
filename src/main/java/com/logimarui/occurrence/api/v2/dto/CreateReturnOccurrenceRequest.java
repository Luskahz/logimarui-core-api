package com.logimarui.occurrence.api.v2.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateReturnOccurrenceRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Positive Long invoiceNumber
) {
}
