package com.logimarui.core.api.service;

import com.logimarui.core.api.domain.read.Cliente;
import com.logimarui.core.api.exception.cliente.ClienteNaoEncontradoException;
import com.logimarui.core.api.repository.read.ClienteReadRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteReadRepository clienteReadRepository;

    public ClienteService(
            ClienteReadRepository clienteReadRepository
    ){
        this.clienteReadRepository = clienteReadRepository;
    }




    public Cliente buscar(Long codigo){
        return clienteReadRepository.buscar(codigo).orElseThrow(()->
                new ClienteNaoEncontradoException("O código PDV fornecido não retornou clientes do banco diretorio"));

    }
}
