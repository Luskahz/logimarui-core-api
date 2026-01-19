package com.logimarui.replenishment.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRoute {
    private Long codigo;
    private Long codigoMotorista;
    private String placaVeiculo;
    private List<Long> clientes;
}
