package com.logimarui.reposicoes.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DamageRecord {
    private String storageReference;//path, key, UUID ou outro, vou depender de como vou implementar no infra
    Instant createdAt;
    String contentType;
    long size;
}
