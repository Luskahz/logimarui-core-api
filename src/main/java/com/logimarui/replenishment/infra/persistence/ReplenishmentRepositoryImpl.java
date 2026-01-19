package com.logimarui.replenishment.infra.persistence;

import com.logimarui.replenishment.core.domain.model.ReplenishmentLine;
import com.logimarui.replenishment.core.repository.ReplenishmentRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class ReplenishmentRepositoryImpl implements ReplenishmentRepository {
    @Override
    public Optional<ReplenishmentLine> montarOcorrencia(Long codigoNotaFiscal, Long codigoProduto) {
        return Optional.empty();
    }

    @Override
    public Boolean registrarReposicao(Long codigoCliente, Long codigoMapa, Long codigoNotaFiscal, LocalDate dataRegistroAplicativo, LocalTime horarioRegistroAplicativo, List<ReplenishmentLine> listaOcorrenciasReposicao) {
        return null;
    }
}
