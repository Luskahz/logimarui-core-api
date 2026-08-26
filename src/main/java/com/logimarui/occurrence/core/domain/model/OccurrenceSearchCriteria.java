package com.logimarui.occurrence.core.domain.model;

import com.logimarui.occurrence.core.domain.enums.OccurrenceStatus;
import com.logimarui.occurrence.core.domain.enums.OccurrenceType;

public record OccurrenceSearchCriteria(
        Long customerId,
        Long invoiceNumber,
        OccurrenceType type,
        OccurrenceStatus status,
        Boolean problemResolved
) {
}
