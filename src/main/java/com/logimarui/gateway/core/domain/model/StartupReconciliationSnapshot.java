package com.logimarui.gateway.core.domain.model;

import java.time.Instant;

public record StartupReconciliationSnapshot(
        StartupReconciliationStatus status,
        String currentServiceId,
        String errorMessage,
        Instant startedAt,
        Instant finishedAt
) {
}
