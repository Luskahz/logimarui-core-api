package com.logimarui.core.api.domain.read.entrega;

import com.logimarui.core.api.domain.read.Equipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mapa {
    private Long codigo;

    private String placaFrota;

    private LocalDate dataSaida;
    private LocalTime horaSaida;
    private LocalDate dataEntrada;
    private LocalTime horaEntrada;
    private LocalDate dataFechado;
    private LocalTime horaFechado;
    private Equipe equipe;
    private Boolean statusTR;
    private Boolean statusTML;
    private Boolean statusTI;
    private Boolean statusJL;
    private List<Entrega> entregas;
    private BigDecimal totalPeso;
    private BigDecimal totalHL;
}
