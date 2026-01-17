package com.logimarui.reposicoes.core.repository;

import java.util.Optional;

public interface POSRepository {
    Optional<POSRepository> buscar(Long codigoCliente);
}
