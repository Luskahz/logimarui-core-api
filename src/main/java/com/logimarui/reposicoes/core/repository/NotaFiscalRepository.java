package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.NotaFiscal;

import java.util.Optional;

public interface NotaFiscalRepository {
    Optional<NotaFiscal> buscar(Long codigoNotaFiscal, int codigoSerieNotaFiscal);
}
