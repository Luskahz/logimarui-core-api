package com.logimarui.all.core.api.dto.write.ocorrencia;

import java.math.BigDecimal;
import java.util.Map;

public record MontarOcorrenciaResponseDTO(
        Long codigoCliente,
        Long mapa,
        Map<Long, Integer> pedidos,
        Long codigoMotorista,
        Long codigoVendedor,
        BigDecimal totalVolume,
        BigDecimal totalValor,
        BigDecimal totalPeso
) {
}
