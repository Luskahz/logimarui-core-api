package com.logimarui.all.core.api.dto.motorista;

public record MotoristaResponseDTO(
        Long id,
        Long employeeId,
        String nome,
        String cpf,
        String cluster
) {}
