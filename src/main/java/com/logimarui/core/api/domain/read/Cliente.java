package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {
    private Long codigo;
    private String fantasia;
    private String cpf;
    private String cnpj;
    private String razaoSocial;
    private BigDecimal latitude;
    private BigDecimal longitude;

}
