package com.logimarui.journey.api.v1.dto;

import java.time.LocalDate;

/** Identity of one map and one crew member. Multiple maps per day are distinct. */
public record OperationalContext(
        LocalDate date,
        Long map,
        String mapOrigin,
        Long registration,
        Long employeeCode,
        String role,
        String employeeName,
        Long vehicle,
        String plate,
        String fleet,
        Long mapDriverCode,
        Long routeSupervisorCode,
        String routeSupervisorName
) {
}
