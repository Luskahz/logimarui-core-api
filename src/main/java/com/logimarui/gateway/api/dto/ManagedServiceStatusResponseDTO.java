package com.logimarui.gateway.api.dto;

import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.domain.model.ServiceType;

public record ManagedServiceStatusResponseDTO(
        String id,
        String pathPrefix,
        ServiceType type,
        int preferredPort,
        boolean requiresAuthentication,
        String portEnvironmentVariable,
        boolean startOnBoot,
        boolean running,
        ServiceRuntime runtime
) {
}
