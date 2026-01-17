package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> buscar(Long codigoProduto);

}
