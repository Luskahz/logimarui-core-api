package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Motorista;

import java.util.Optional;

public interface MotoristaRepository {
    Optional<Motorista> buscar(Long codigoMotorista);
}
