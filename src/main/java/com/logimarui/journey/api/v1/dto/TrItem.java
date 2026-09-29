package com.logimarui.journey.api.v1.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrItem(
        OperationalContext context,
        LocalDateTime mapDepartureAt,
        LocalDateTime mapReturnAt,
        LocalDateTime snapshotAt,
        Long plannedSeconds,
        IndicatorResult tr,
        BigDecimal dispersion,
        Expurge expurge
) {
}
