package com.logimarui.all.core.api.dto.write.ocorrencia;

import com.logimarui.all.core.api.domain.write.enums.StatusOcorrencia;

import java.math.BigDecimal;
import java.util.Map;

public record ConfirmarOcorrenciaDTO(
        Long codigoCliente,
        Long mapa,
        Map<Long, Integer> pedidos,
        Long codigoMotorista,
        Long codigoVendedor,
        BigDecimal totalVolume,
        BigDecimal totalValor,
        BigDecimal totalPeso,
        String motivo,
        String observacao,
        StatusOcorrencia status

) {
}
