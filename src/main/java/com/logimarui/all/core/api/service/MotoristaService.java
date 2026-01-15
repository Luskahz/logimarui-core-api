package com.logimarui.all.core.api.service;

import com.logimarui.all.core.api.domain.read.Motorista;
import com.logimarui.all.core.api.exception.motorista.MotoristaNaoEncontradoException;
import com.logimarui.all.core.api.repository.read.MotoristaReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MotoristaService {
    private MotoristaReadRepository motoristaReadRepository;

    public Motorista buscar(Long codigo){
        return motoristaReadRepository.buscar(codigo).orElseThrow(()->
                new MotoristaNaoEncontradoException("Motorista não encontrado para o código: " + codigo));
    }

}
