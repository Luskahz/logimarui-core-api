package com.logimarui.gateway.core.port;

import com.logimarui.gateway.core.domain.model.ManagedService;

import java.util.List;
import java.util.Optional;

public interface ManagedServiceProvider {

    List<ManagedService> findAll();

    Optional<ManagedService> findById(String id);
}