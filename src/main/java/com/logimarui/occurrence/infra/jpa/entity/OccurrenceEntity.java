package com.logimarui.occurrence.infra.jpa.entity;

import com.logimarui.occurrence.core.domain.enums.OccurrenceStatus;
import com.logimarui.occurrence.core.domain.enums.OccurrenceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "occurrences", indexes = {
        @Index(name = "idx_occurrences_customer_id", columnList = "customer_id"),
        @Index(name = "idx_occurrences_invoice_number", columnList = "invoice_number"),
        @Index(name = "idx_occurrences_type_status", columnList = "type,status")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OccurrenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "invoice_number", nullable = false)
    private Long invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private OccurrenceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OccurrenceStatus status;

    @Column(name = "problem_resolved", nullable = false)
    private boolean problemResolved;

    @Column(name = "reason", length = 120)
    private String reason;

    @Column(name = "observation", length = 2000)
    private String observation;

    @Column(name = "transfer_possible", nullable = false)
    private boolean transferPossible;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "return_confirmed_at")
    private Instant returnConfirmedAt;

    @Column(name = "reverted_at")
    private Instant revertedAt;
}
