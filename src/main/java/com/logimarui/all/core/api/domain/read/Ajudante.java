package com.logimarui.all.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ajudante {
    private Long codigo;
    private Long employeeId;
    private String nome;
    private String CPF;
}
