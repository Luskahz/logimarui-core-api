package com.logimarui.gateway.core.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ServiceRuntime {

    private final String serviceId;
    private final int port;
    private final Long rootPid;
    private final Long listenerPid;
    private final ServiceStatus status;
    private final Instant startedAt;

    @JsonCreator
    public ServiceRuntime(
            @JsonProperty("serviceId") String serviceId,
            @JsonProperty("port") int port,
            @JsonProperty("rootPid") Long rootPid,
            @JsonProperty("listenerPid") Long listenerPid,
            @JsonProperty("status") ServiceStatus status,
            @JsonProperty("startedAt") Instant startedAt
    ) {
        this.serviceId = serviceId;
        this.port = port;
        this.rootPid = rootPid;
        this.listenerPid = listenerPid;
        this.status = status;
        this.startedAt = startedAt;
    }
}