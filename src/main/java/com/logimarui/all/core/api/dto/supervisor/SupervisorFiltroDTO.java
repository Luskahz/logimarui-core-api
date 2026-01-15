package com.logimarui.all.core.api.dto.supervisor;

import java.time.LocalTime;

public record SupervisorFiltroDTO(
        String nome,
        LocalTime matinal
) {
}
