package com.logimarui.journey.api.v1.dto;

import com.logimarui.operationalread.core.model.Expurge;
import com.logimarui.operationalread.core.model.OperationalContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JlItem(
        OperationalContext context,
        String mode,
        LocalDate pointJourneyDate,
        String pointOrigin,
        LocalDateTime morningAt,
        LocalDateTime pointEntryAt,
        LocalDateTime mapDepartureAt,
        LocalDateTime mapReturnAt,
        LocalDateTime physicalCloseAt,
        LocalDateTime financialCloseAt,
        LocalDateTime pointExitAt,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime snapshotAt,
        String operationalStage,
        IndicatorResult tml,
        IndicatorResult tr,
        IndicatorResult physicalClose,
        IndicatorResult financialClose,
        IndicatorResult ti,
        IndicatorResult jl,
        Long componentSumSeconds,
        Long divergenceSeconds,
        Expurge expurge
) {
}
