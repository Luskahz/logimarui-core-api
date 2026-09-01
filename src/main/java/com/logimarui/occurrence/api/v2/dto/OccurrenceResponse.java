package com.logimarui.occurrence.api.v2.dto;

import com.logimarui.occurrence.core.domain.enums.OccurrenceStatus;
import com.logimarui.occurrence.core.domain.enums.OccurrenceType;

import java.time.Instant;

public record OccurrenceResponse(
        Long id,
        Long customerId,
        Long invoiceNumber,
        OccurrenceType type,
        OccurrenceStatus status,
        boolean problemResolved,
        String reason,
        String observation,
        boolean transferPossible,
        Instant createdAt,
        Instant updatedAt,
        Instant returnConfirmedAt,
        Instant revertedAt
) {
}
