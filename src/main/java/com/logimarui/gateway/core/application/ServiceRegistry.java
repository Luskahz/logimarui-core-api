package com.logimarui.gateway.core.application;

import com.logimarui.gateway.core.domain.model.ServiceRoute;
import com.logimarui.gateway.core.port.ServiceRouteProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRegistry {

    private final ServiceRouteProvider serviceRouteProvider;

    public List<ServiceRoute> findAll() {
        return serviceRouteProvider.findAll();
    }
}