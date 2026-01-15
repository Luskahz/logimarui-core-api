package com.logimarui.all.core.api.service;

import com.logimarui.all.core.api.domain.read.Vendedor;
import com.logimarui.all.core.api.exception.vendedor.VendedorNaoEncontradoException;
import com.logimarui.all.core.api.repository.read.VendedorReadRepository;
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
