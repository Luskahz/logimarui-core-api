package com.logimarui.reposicoes.core.domain.model;

import java.time.LocalDate;
import java.util.List;

public class NotaFiscal {
    private Long codigoNotaFiscal;
    private int codigoSerieNotaFiscal;
    private LocalDate emissao;
    private Long codigoMapa;
    private List<Produto> produtos;
}
