package com.logimarui.core.api.domain.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipe {
    public Mapa mapa;
    public LocalDate dataEquipe;
    public Motorista motorista;
    public Ajudante ajudante1;
    public Ajudante ajudante2;
    public Supervisor supervisor;
}
