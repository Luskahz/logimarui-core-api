package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Produto;

import java.util.Optional;

public interface ProdutoRepository {
    Optional<Produto> buscar(Long codigoProduto);

}
