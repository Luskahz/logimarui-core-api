package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Frota {
    private String placa;
    private Long codigo;
    private String tipo;
    private String carroceria;
    private String marca_mod;
    private String fabricante;
}
