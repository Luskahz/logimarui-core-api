package com.logimarui.all.core.api.dto.motorista;

public record MotoristaResponseDTO(
        Long codigo,
        Long matricula,
        String nome,
        String cpf,
        String cluster
) {}
