package com.logimarui.all.core.api.domain.write.ocorrencia;

import com.logimarui.all.core.api.domain.write.enums.StatusOcorrencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevolucaoTotal {
    private Long codigo;
    private Long codigoCliente;
    private Long Mapa;
    private Long nota;
    private Long codigoMotorista;
    private Long codigoVendedor;
    private LocalTime horaRegistro;
    private LocalDate dataRegistro;
    private BigDecimal totalVolume;
    private BigDecimal totalValor;
    private BigDecimal totalPeso;
    private String motivo;
    private String observacao;
    private StatusOcorrencia status;
}
