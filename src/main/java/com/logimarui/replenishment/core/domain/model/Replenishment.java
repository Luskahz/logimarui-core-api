package com.logimarui.replenishment.core.domain.model;

import com.logimarui.replenishment.core.domain.enums.ReplenishmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Replenishment {
    private Long codigoReposicao;
    private Long codigoCliente;
    private Long codigoMapa;
    private Long codigoNotaFiscal;
    private int codigoSerieNotaFiscal;
    private LocalDate dataOcorrencia;
    private LocalTime horarioOcorrencia;
    private ReplenishmentStatus statusReposicao;
    private List<ReplenishmentLine> listaOcorrenciasReposicao;


}
