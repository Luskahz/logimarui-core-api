package com.logimarui.journey.core.model;

/** All durations are numeric seconds; each status and outcome is independently nullable. */
public record IndicatorResult(
        Long secondsDifference,
        Long seconds,
        Long targetSeconds,
        String lifecycleStatus,
        String targetStatus,
        Boolean achieved,
        Boolean temporalOrderAnomaly
) {
}
