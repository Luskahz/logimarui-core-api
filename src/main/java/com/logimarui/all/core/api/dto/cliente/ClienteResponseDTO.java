package com.logimarui.all.core.api.dto.cliente;

import java.math.BigDecimal;

public record ClienteResponseDTO (
    Long codigo,
    String fantasia,
    String CPF,
    String CNPJ,
    String razaoSocial,
    BigDecimal latitude,
    BigDecimal longitude
){}
