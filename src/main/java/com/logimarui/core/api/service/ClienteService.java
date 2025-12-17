package com.logimarui.core.api.service;

import com.logimarui.core.api.domain.read.Cliente;
import com.logimarui.core.api.exception.cliente.ClienteNaoEncontradoException;
import com.logimarui.core.api.repository.read.ClienteReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClienteService {
    private final ClienteReadRepository clienteReadRepository;


    public Cliente buscar(Long codigo){
        return clienteReadRepository.buscar(codigo).orElseThrow(()->
                new ClienteNaoEncontradoException("Cliente não encontrado para o código: " + codigo));
    }
}
