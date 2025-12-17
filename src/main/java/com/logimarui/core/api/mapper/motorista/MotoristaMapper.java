package com.logimarui.core.api.mapper.motorista;

import com.logimarui.core.api.domain.read.Motorista;
import com.logimarui.core.api.dto.motorista.MotoristaResponseDTO;

public class MotoristaMapper {
    public static MotoristaResponseDTO toResponse(Motorista motorista){
        return new MotoristaResponseDTO(
                motorista.getCodigo(),
                motorista.getMatricula(),
                motorista.getNome(),
                motorista.getCpf(),
                motorista.getCluster()
        );
    }
}
