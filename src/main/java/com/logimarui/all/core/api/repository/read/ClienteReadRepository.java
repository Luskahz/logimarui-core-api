package com.logimarui.all.core.api.repository.read;

import com.logimarui.all.core.api.domain.read.Cliente;

import java.util.Optional;

public interface ClienteReadRepository {
    Optional<Cliente> buscar(Long codigo);

}
