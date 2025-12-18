package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipe {
    private Long codigoFrota;
    private Long codigoMotorista;
    private Long codigoAjudante1;
    private Long codigoAjudante2;
    private Long codigoSupervisor;
}
