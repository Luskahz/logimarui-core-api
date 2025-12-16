package com.logimarui.core.api.dto.cliente;

public record ClienteFiltroDTO (
        String CPF,
        String CNPJ,
        String fantasia,
        String razaoSocial
){}
