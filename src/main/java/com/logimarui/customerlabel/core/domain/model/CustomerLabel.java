package com.logimarui.customerlabel.core.domain.model;

import java.time.Instant;

public record CustomerLabel(
        Long customerId,
        String label,
        Instant generatedAt
) {
}
