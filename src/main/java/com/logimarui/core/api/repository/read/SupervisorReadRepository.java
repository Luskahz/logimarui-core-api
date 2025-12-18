package com.logimarui.core.api.repository.read;

import com.logimarui.core.api.domain.read.Supervisor;

import java.util.Optional;

public interface SupervisorReadRepository {
    Optional<Supervisor> buscar(Long codigo);
}
