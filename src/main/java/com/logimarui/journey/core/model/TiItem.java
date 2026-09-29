package com.logimarui.journey.core.model;

import com.logimarui.operationalread.core.model.Expurge;
import com.logimarui.operationalread.core.model.OperationalContext;

import java.time.LocalDateTime;

public record TiItem(
        OperationalContext context,
        String mode,
        LocalDateTime mapDepartureAt,
        LocalDateTime vehicleEntryAt,
        LocalDateTime physicalCloseAt,
        LocalDateTime financialCloseAt,
        String pointOrigin,
        LocalDateTime pointExitAt,
        LocalDateTime endedAt,
        LocalDateTime snapshotAt,
        String operationalStage,
        IndicatorResult physicalClose,
        IndicatorResult financialClose,
        IndicatorResult ti,
        Long sourcePhysicalSeconds,
        Long sourceFinancialSeconds,
        Long sourceInternalSeconds,
        Expurge expurge
) {
}
