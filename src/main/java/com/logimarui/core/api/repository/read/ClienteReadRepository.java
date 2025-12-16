package com.logimarui.core.api.repository.read;

import com.logimarui.core.api.domain.read.Cliente;

import java.util.Optional;

public interface ClienteReadRepository {
    Optional<Cliente> buscar(Long codigo);

}
