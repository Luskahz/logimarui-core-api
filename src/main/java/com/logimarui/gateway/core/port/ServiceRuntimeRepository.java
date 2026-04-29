package com.logimarui.gateway.core.port;

import com.logimarui.gateway.core.domain.model.ServiceRuntime;

import java.util.List;
import java.util.Optional;

public interface ServiceRuntimeRepository {

    void save(ServiceRuntime runtime);

    Optional<ServiceRuntime> findByServiceId(String serviceId);

    List<ServiceRuntime> findAll();

    void deleteByServiceId(String serviceId);
}