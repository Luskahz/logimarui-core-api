package com.logimarui.core.api.service;

import com.logimarui.core.api.domain.read.Vendedor;
import com.logimarui.core.api.exception.vendedor.VendedorNaoEncontradoException;
import com.logimarui.core.api.repository.read.VendedorReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VendedorService {
    private final VendedorReadRepository vendedorReadRepository;

    public Vendedor buscar(Long codigo){
        return vendedorReadRepository.buscar(codigo).orElseThrow(()->
                new VendedorNaoEncontradoException( "Vendedor não encontrado para o código informado: " + codigo));
    }
}
