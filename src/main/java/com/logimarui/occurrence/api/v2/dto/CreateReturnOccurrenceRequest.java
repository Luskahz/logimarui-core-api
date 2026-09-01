package com.logimarui.occurrence.api.v2.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateReturnOccurrenceRequest(
        @NotNull @Positive Long customerId,
        @NotNull @Positive Long invoiceNumber,
        @NotBlank @Size(max = 120) String reason,
        @NotBlank @Size(max = 2000) String observation,
        @NotNull Boolean transferPossible
) {
}
