package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Mapa;

import java.util.Optional;

public interface MapaRepository {
    Optional<Mapa> buscar(Long codigoMapa);
}
