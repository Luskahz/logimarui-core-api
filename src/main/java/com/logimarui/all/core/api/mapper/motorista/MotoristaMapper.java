package com.logimarui.all.core.api.mapper.motorista;

import com.logimarui.all.core.api.domain.read.Motorista;
import com.logimarui.all.core.api.dto.motorista.MotoristaResponseDTO;

public class MotoristaMapper {
    public static MotoristaResponseDTO toResponse(Motorista motorista){
        return new MotoristaResponseDTO(
                motorista.getCodigo(),
                motorista.getEmployeeId(),
                motorista.getNome(),
                motorista.getCpf(),
                motorista.getCluster()
        );
    }
}
