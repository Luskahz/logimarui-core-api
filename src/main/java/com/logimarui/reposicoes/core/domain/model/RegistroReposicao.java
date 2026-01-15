package com.logimarui.reposicoes.core.domain.model;

import com.logimarui.reposicoes.core.domain.enums.StatusReposicao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RegistroReposicao {
    private Long codigoCliente;
    private Long codigoMapa;
    private Long codigoNotaFiscal;
    private int codigoSerieNotaFiscal;
    private LocalDate dataOcorrencia;
    private LocalTime horarioOcorrencia;
    private StatusReposicao statusReposicao;
    private List<OcorrenciaReposicao> listaOcorrenciasReposicao;

}
