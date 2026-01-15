package com.logimarui.all.core.api.domain.read;

import com.logimarui.all.core.api.domain.read.entrega.Mapa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Reposicao {
    private Long codigo;
    private Cliente cliente;
    private LocalDate dataSolicitacao;
    private String Status;
    private LocalDate dataAcao;

    private String statusReposicao;

    private Mapa mapaOrigem;
    private String notaOrigem;
    private Produto produtoOrigem;
    private Integer quantidadeOrigem;

    private Mapa mapaReposicao;
    private String notaReposicao;
    private Produto produtoReposicao;
    private Integer quantidadeReposicao;
    private String usuarioAcao;

    private Motorista motorista;
    private Ajudante ajudante1;
    private Ajudante ajudante2;

}
