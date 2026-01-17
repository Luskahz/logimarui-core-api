package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.DeliveryRoute;

import java.util.Optional;

public interface DeliveryRouteRepository {
    Optional<DeliveryRoute> buscar(Long codigoMapa);
}
