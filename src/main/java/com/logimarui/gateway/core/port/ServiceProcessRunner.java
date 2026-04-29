package com.logimarui.gateway.core.port;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;

public interface ServiceProcessRunner {

    boolean supports(ManagedService service);

    ServiceRuntime start(ManagedService service);

    void stop(ManagedService service, ServiceRuntime runtime);
}