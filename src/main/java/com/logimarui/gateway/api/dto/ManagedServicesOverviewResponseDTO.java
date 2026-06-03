package com.logimarui.gateway.api.dto;

import com.logimarui.gateway.core.domain.model.StartupReconciliationSnapshot;

import java.util.List;

public record ManagedServicesOverviewResponseDTO(
        StartupReconciliationSnapshot startup,
        List<ManagedServiceStatusResponseDTO> services
) {
}
