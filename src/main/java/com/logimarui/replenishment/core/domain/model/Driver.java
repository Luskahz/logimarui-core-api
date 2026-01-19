package com.logimarui.replenishment.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver {
    private Long codigoMotorista;
    private String nomeMotorista;
}
