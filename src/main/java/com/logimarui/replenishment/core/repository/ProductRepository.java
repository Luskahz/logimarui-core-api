package com.logimarui.replenishment.core.repository;

import com.logimarui.replenishment.core.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> buscar(Long codigoProduto);

}
