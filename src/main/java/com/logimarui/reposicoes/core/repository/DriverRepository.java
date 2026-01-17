package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Driver;

import java.util.Optional;

public interface DriverRepository {
    Optional<Driver> buscar(Long codigoMotorista);
}
