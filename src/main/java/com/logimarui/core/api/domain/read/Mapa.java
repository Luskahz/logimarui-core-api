package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mapa {
    private Long numMapa;
    private String placaFrota;
    private LocalDateTime Saida;
    private LocalDateTime entrada;
    private LocalDateTime fechado;
    private Long motorista;
    private Long ajudante1;
    private Long ajudante2;
    private Boolean statusTR;
    private Boolean statusTML;
    private Boolean statusTI;
    private Boolean statusJL;
    private Integer qtdClientes;
    private Float pesoTotal;
    private  Float totalHl;
}
