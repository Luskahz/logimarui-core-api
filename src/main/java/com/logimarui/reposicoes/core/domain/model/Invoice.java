package com.logimarui.reposicoes.core.domain.model;

import com.logimarui.reposicoes.core.domain.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    private Long codigoNotaFiscal;
    private int codigoSerieNotaFiscal;
    private LocalDate emissao;
    private Long codigoMapa;
    private List<Long> produtos;
    private DeliveryStatus statusEntrega;
}
