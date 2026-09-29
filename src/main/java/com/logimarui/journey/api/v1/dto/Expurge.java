package com.logimarui.journey.api.v1.dto;

/** Operational exclusion markers are context only; they never change KPI results. */
public record Expurge(
        Boolean present,
        Long count,
        String ids,
        String types,
        String reasons,
        String observations,
        Flags flags
) {
    public record Flags(
            Boolean stoppedMap,
            Boolean considerRv,
            Boolean recharge,
            Boolean historical,
            Boolean general
    ) {
    }
}
