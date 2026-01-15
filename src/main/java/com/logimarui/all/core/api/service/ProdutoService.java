package com.logimarui.all.core.api.service;


import com.logimarui.all.core.api.domain.read.Produto;
import com.logimarui.all.core.api.exception.produto.ProdutoNaoEncontradoException;
import com.logimarui.all.core.api.repository.read.ProdutoReadRepository;
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
