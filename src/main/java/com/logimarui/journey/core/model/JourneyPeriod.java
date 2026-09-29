package com.logimarui.journey.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record JourneyPeriod<T>(
        LocalDate from,
        LocalDate to,
        LocalDateTime snapshotAt,
        List<T> items
) {
}
