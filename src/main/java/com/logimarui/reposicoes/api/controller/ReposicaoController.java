package com.logimarui.reposicoes.api.controller;


import com.logimarui.reposicoes.core.domain.enums.MotivosReposicao;
import com.logimarui.reposicoes.core.domain.enums.UnidadeQuantidade;
import com.logimarui.reposicoes.core.domain.model.OcorrenciaReposicao;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reposicoes/")
@AllArgsConstructor
public class ReposicaoController {
    private final ReposicaoService reposicaoService;


    @GetMapping("/mapa/buscar/{codigoMapa}")
    public MapaResponseDTO buscarMapa(@PathVariable long codigoMapa){
        return new MapaMapper().toResponse(reposicaoService.buscarMapa(codigoMapa));
    }// a busca do mapa vai ser chamada no login, apos identificar a frota do motorista
    // ou tambem pode ser puxado quando ele solicitar a troca de mapa no campo do forms
    // o mapa só podera ser trocado caso o veiculo do motorista esteja associado a ele
    // ou seja, raramente, porem possivel em caso de recarga ou alguma anomaria 03.02.37

    @GetMapping("/notaFiscal/buscar/")
    public NotaFiscalResponseDTO buscarNotaFiscalByCodigo(
            @RequestParam Long codigoNotaFiscal,
            @RequestParam int codigoSerieNotaFiscal
    ){
        return new NotaFiscalMapper().toResponse(reposicaoService.buscarNotaFiscalByCodigo(codigoNotaFiscal,codigoSerieNotaFiscal));
    }

    @GetMapping("/produto/buscar/{codigoProduto}")
    public ProdutoResponseDTO buscarProdutoDaNotaByCodigo(
            @PathVariable Long codigoProduto,
            @RequestParam Long codigoNotaFiscal,
            @RequestParam int codigoSerieNotaFiscal
    ){
        return new produtoMapper().toResponse(reposicaoService.buscarProdutoDaNotaByCodigo(codigoProduto, codigoNotaFiscal, codigoSerieNotaFiscal));
    }

    GetMapping("/ocorrenciaReposicao/")
    public ocorrenciaReposicaoResponseDTO buscarDadosParaOcorrencia(
            @RequestParam Long codigoNotaFiscal,
            @RequestParam int codigoSerieNotaFiscal
    ){
     return new OcorrenciaReposicaoMapper().toResponse(reposicaoService.buscar)
    }

    PostMapping("/ocorrenciaReposicao/")
    public ocorrenciaResponseDTO registrarReposicao(
            @RequestParam Long codigoMapa,
            @RequestParam Long codigoCliente,
            @RequestParam Long codigoNotaFiscal,
            @RequestParam int codigoSerieNotaFiscal,
            @RequestParam Long codigoProduto,
            @RequestParam List<OcorrenciaReposicao> ocorrencias

            )
}
