package com.logimarui.journey.api.v1.dto;

import com.logimarui.operationalread.core.model.Expurge;
import com.logimarui.operationalread.core.model.OperationalContext;

import java.time.LocalDateTime;

public record TmlItem(
        OperationalContext context,
        String mode,
        String pointOrigin,
        LocalDateTime entryAt,
        LocalDateTime morningAt,
        LocalDateTime mapDepartureAt,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime snapshotAt,
        IndicatorResult tml,
        Checklist loadChecklist,
        Checklist maintenanceChecklist,
        Long totalChecklistSeconds,
        Expurge expurge
) {
    public record Checklist(
            Long count,
            Long totalSeconds,
            Long effectiveSeconds,
            LocalDateTime effectiveStartedAt,
            LocalDateTime effectiveEndedAt,
            Boolean adherent
    ) {
    }
}
