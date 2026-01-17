package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Vehicle;

import java.util.Optional;

public interface VehicleRepository {
    Optional<Vehicle> buscar(Long codigoVeiculo);
}
