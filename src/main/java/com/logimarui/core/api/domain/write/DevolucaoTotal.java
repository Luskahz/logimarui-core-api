package com.logimarui.core.api.domain.write;

import com.logimarui.core.api.domain.read.Cliente;
import com.logimarui.core.api.domain.read.Mapa;
import com.logimarui.core.api.domain.read.Motorista;
import com.logimarui.core.api.domain.read.Vendedor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevolucaoTotal {
    private Long codigo;
    private Cliente cliente;
    private Mapa mapa;
    private Long nota;
    private Motorista motorista;
    private Vendedor vendedor;
    private LocalTime horaRegistro;
    private LocalDate dataRegistro;
    private Float volumeTotal;
    private Double valorTotal;
    private String motivo;
    private String observacao;
}
