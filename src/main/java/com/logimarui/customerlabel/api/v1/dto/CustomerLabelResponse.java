package com.logimarui.customerlabel.api.v1.dto;

import java.time.Instant;

public record CustomerLabelResponse(
        Long customerId,
        String label,
        Instant generatedAt
) {
}
