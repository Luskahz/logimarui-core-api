package com.logimarui.journey.api.v1.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record JourneyPeriodResponse<T>(
        LocalDate from,
        LocalDate to,
        LocalDateTime snapshotAt,
        List<T> items
) {
}
