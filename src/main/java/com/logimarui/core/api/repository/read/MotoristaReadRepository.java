package com.logimarui.core.api.repository.read;

import com.logimarui.core.api.domain.read.Motorista;

import java.util.Optional;

public interface MotoristaReadRepository {
    Optional<Motorista> buscar(Long codigo);
}
