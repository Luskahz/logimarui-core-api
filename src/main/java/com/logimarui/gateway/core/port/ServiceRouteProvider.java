package com.logimarui.gateway.core.port;

import com.logimarui.gateway.core.domain.model.ServiceRoute;

import java.util.List;

public interface ServiceRouteProvider {

    List<ServiceRoute> findAll();

}