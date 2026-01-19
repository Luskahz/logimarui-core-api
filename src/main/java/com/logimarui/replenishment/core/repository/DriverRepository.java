package com.logimarui.replenishment.core.repository;

import com.logimarui.replenishment.core.domain.model.Driver;

import java.util.Optional;

public interface DriverRepository {
    Optional<Driver> buscar(Long codigoMotorista);
}
