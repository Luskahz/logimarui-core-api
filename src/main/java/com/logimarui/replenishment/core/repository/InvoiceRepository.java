package com.logimarui.replenishment.core.repository;

import com.logimarui.replenishment.core.domain.model.Invoice;

import java.util.Optional;

public interface InvoiceRepository {
    Optional<Invoice> buscar(Long codigoNotaFiscal, int codigoSerieNotaFiscal);
}
