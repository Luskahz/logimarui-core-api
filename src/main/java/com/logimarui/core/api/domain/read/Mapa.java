package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mapa {
    private Long codigoMapa;
    private String placaFrota;

    private LocalDate dataSaida;
    private LocalTime horaSaida;
    private LocalDate dataEntrada;
    private LocalTime horaEntrada;
    private LocalDate dataFechado;
    private LocalTime horaFechado;

    private Motorista motorista;
    private Ajudante ajudante1;
    private Ajudante ajudante2;
    private Boolean statusTR;
    private Boolean statusTML;
    private Boolean statusTI;
    private Boolean statusJL;
    private Integer totalClientes;
    private Float totalPeso;
    private  Float totalHL;
}
