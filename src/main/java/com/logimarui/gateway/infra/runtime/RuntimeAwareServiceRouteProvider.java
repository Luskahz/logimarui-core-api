package com.logimarui.gateway.infra.runtime;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceRoute;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.port.ManagedServiceProvider;
import com.logimarui.gateway.core.port.ServiceRouteProvider;
import com.logimarui.gateway.core.port.ServiceRuntimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RuntimeAwareServiceRouteProvider implements ServiceRouteProvider {

    private final ManagedServiceProvider managedServiceProvider;
    private final ServiceRuntimeRepository serviceRuntimeRepository;

    @Override
    public List<ServiceRoute> findAll() {
        return managedServiceProvider.findAll()
                .stream()
                .flatMap(service ->
                        serviceRuntimeRepository.findByServiceId(service.getId())
                                .map(runtime -> toServiceRoute(service, runtime))
                                .stream()
                )
                .toList();
    }

    private ServiceRoute toServiceRoute(ManagedService service, ServiceRuntime runtime) {
        return new ServiceRoute(
                service.getId(),
                service.getPathPrefix(),
                "http://127.0.0.1:" + runtime.getPort(),
                service.isRequiresAuthentication()
        );
    }
}