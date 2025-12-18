package com.logimarui.core.api.service;

import com.logimarui.core.api.domain.read.Supervisor;
import com.logimarui.core.api.exception.supervisor.SupervisorNaoEncontradoException;
import com.logimarui.core.api.repository.read.SupervisorReadRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupervisorService {
    private final SupervisorReadRepository supervisorReadRepository;

    public Supervisor buscar(Long codigo){
        return supervisorReadRepository.buscar(codigo).orElseThrow(()->
                new SupervisorNaoEncontradoException("Supervisor não encontrado para o codigo: " + codigo));
    }
}
