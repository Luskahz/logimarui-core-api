package com.logimarui.core.api.service;


import com.logimarui.core.api.domain.read.Produto;
import com.logimarui.core.api.exception.produto.ProdutoNaoEncontradoException;
import com.logimarui.core.api.repository.read.ProdutoReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProdutoService {
    private final ProdutoReadRepository produtoReadRepository;

    public Produto buscar(Long codigo){
        return produtoReadRepository.buscar(codigo).orElseThrow(()->
                new ProdutoNaoEncontradoException("Produto não encontrado para o código: " + codigo));
    }

}
