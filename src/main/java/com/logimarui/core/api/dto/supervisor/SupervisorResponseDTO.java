package com.logimarui.core.api.dto.supervisor;

import java.time.LocalTime;

public record SupervisorResponseDTO(
        Long codigo,
        String nome,
        LocalTime matinal
) {
}
