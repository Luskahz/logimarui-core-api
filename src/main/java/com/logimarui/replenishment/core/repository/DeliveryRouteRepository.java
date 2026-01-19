package com.logimarui.replenishment.core.repository;

import com.logimarui.replenishment.core.domain.model.DeliveryRoute;

import java.util.Optional;

public interface DeliveryRouteRepository {
    Optional<DeliveryRoute> buscar(Long codigoMapa);
}
