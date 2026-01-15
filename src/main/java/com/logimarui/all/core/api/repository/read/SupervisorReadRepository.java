package com.logimarui.all.core.api.repository.read;

import com.logimarui.all.core.api.domain.read.Supervisor;

import java.util.Optional;

public interface SupervisorReadRepository {
    Optional<Supervisor> buscar(Long codigo);
}
