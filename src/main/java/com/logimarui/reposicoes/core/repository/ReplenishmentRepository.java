package com.logimarui.reposicoes.core.repository;

import com.logimarui.reposicoes.core.domain.model.ReplenishmentLine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReplenishmentRepository {
    Optional<ReplenishmentLine> montarOcorrencia(Long codigoNotaFiscal, Long codigoProduto);
    //tem que retornar a quantidade de produto especifico na nota.
    //aqui que vou iniciar a exception de quantidade maxima do produto que pode ser reposto pra essa nota

    Boolean registrarReposicao(
                Long codigoCliente,
                Long codigoMapa,
                Long codigoNotaFiscal,
                LocalDate dataRegistroAplicativo,
                LocalTime horarioRegistroAplicativo,
                List<ReplenishmentLine> listaOcorrenciasReposicao);
}
    //aqui eu cou realizar o registro da reposição
    //isso vai ser anotado no banco de dados ao final do registro do motorista,
    //lembrar de registrar a data e hora de lançamento no banco de dados devido
    //a possiveis problemas com internet da equipe

