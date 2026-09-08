package com.logimarui.customerlabel.infra.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "tmp_cluster_clientes", indexes = {
        @Index(name = "idx_tmp_cluster_clientes_generated_at", columnList = "generated_at")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CustomerLabelCacheEntity {
    @Id
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;
}
