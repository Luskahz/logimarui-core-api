package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ajudante {
    private Long codigo;
    private Long matricula;
    private String nome;
    private String CPF;
}
