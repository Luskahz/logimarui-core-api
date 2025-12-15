package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {
    private Long codigo;
    private String fantasia;
    private String CPF;
    private String CNPJ;
    private String rasaoSocial;
    private Float latitude;
    private Float longitude;
}
