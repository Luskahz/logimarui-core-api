package com.logimarui.gateway.core.domain.model;

public record ManagedServiceStatusSnapshot(
        ManagedService service,
        boolean running,
        ServiceRuntime runtime
) {
}
