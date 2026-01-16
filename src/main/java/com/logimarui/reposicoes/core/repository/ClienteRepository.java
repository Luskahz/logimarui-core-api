package com.logimarui.reposicoes.core.repository;

import java.util.Optional;

public interface ClienteRepository {
    Optional<ClienteRepository> buscar(Long codigoCliente);
}
