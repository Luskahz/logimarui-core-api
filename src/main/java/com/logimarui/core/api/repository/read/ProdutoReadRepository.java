package com.logimarui.core.api.repository.read;

import com.logimarui.core.api.domain.read.Produto;

import java.util.Optional;

public interface ProdutoReadRepository {
    Optional<Produto> buscar(Long codigo);
}
