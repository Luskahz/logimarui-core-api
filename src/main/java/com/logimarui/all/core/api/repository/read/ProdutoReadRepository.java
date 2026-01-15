package com.logimarui.all.core.api.repository.read;

import com.logimarui.all.core.api.domain.read.Produto;

import java.util.Optional;

public interface ProdutoReadRepository {
    Optional<Produto> buscar(Long codigo);
}
