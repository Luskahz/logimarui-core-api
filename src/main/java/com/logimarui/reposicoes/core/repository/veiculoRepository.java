package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Veiculo;

import java.util.Optional;

public interface veiculoRepository {
    Optional<Veiculo> buscar(Long codigoVeiculo);
}
