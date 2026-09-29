package com.logimarui.operationalread.core.model;

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
