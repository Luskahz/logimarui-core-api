package com.logimarui.replenishment.core.repository;

import com.logimarui.replenishment.core.domain.model.Vehicle;

import java.util.Optional;

public interface VehicleRepository {
    Optional<Vehicle> buscar(Long codigoVeiculo);
}
