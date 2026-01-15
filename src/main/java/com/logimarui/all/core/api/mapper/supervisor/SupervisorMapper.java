package com.logimarui.all.core.api.mapper.supervisor;

import com.logimarui.all.core.api.domain.read.Supervisor;
import com.logimarui.all.core.api.dto.supervisor.SupervisorResponseDTO;

public class SupervisorMapper {
    public static SupervisorResponseDTO toResponse(Supervisor supervisor){
        return new SupervisorResponseDTO(
                supervisor.getCodigo(),
                supervisor.getNome(),
                supervisor.getMatinal()
        );
    }
}
