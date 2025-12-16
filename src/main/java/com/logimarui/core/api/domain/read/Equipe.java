package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipe {
    private Mapa mapa;
    private LocalDate dataEquipe;
    private Motorista motorista;
    private Ajudante ajudante1;
    private Ajudante ajudante2;
    private Supervisor supervisor;
}
