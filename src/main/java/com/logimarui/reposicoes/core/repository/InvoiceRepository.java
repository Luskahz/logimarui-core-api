package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.Invoice;

import java.util.Optional;

public interface InvoiceRepository {
    Optional<Invoice> buscar(Long codigoNotaFiscal, int codigoSerieNotaFiscal);
}
