package com.logimarui.reposicoes.core.domain.model;

import com.logimarui.reposicoes.core.domain.enums.MotivosReposicao;
import com.logimarui.reposicoes.core.domain.enums.UnidadeQuantidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OcorrenciaReposicao {
    private Long codigoProduto;
    private MotivosReposicao motivoReposicao;
    private UnidadeQuantidade unidade;
    private int quantidade;
    private RegistroAvaria imagemAvaria;

}
