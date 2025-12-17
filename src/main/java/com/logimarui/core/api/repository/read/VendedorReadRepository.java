package com.logimarui.core.api.repository.read;

import com.logimarui.core.api.domain.read.Vendedor;

import java.util.Optional;

public interface VendedorReadRepository{
    Optional<Vendedor> buscar(Long codigo);
}

