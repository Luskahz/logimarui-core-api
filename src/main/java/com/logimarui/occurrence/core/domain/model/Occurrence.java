package com.logimarui.occurrence.core.domain.model;

import com.logimarui.occurrence.core.domain.enums.OccurrenceStatus;
import com.logimarui.occurrence.core.domain.enums.OccurrenceType;

import java.time.Instant;
import java.util.Objects;

public class Occurrence {

    private final Long id;
    private final Long customerId;
    private final Long invoiceNumber;
    private final OccurrenceType type;
    private OccurrenceStatus status;
    private boolean problemResolved;
    private final String reason;
    private final String observation;
    private final boolean transferPossible;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant returnConfirmedAt;
    private Instant revertedAt;

    private Occurrence(
            Long id,
            Long customerId,
            Long invoiceNumber,
            OccurrenceType type,
            OccurrenceStatus status,
            boolean problemResolved,
            String reason,
            String observation,
            boolean transferPossible,
            Instant createdAt,
            Instant updatedAt,
            Instant returnConfirmedAt,
            Instant revertedAt
    ) {
        this.id = id;
        this.customerId = requirePositive(customerId, "customerId");
        this.invoiceNumber = requirePositive(invoiceNumber, "invoiceNumber");
        this.type = Objects.requireNonNull(type, "Occurrence type cannot be null");
        this.status = Objects.requireNonNull(status, "Occurrence status cannot be null");
        this.problemResolved = problemResolved;
        this.reason = reason;
        this.observation = observation;
        this.transferPossible = transferPossible;
        this.createdAt = Objects.requireNonNull(createdAt, "Occurrence createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Occurrence updatedAt cannot be null");
        this.returnConfirmedAt = returnConfirmedAt;
        this.revertedAt = revertedAt;
    }

    public static Occurrence createReturn(Long customerId, Long invoiceNumber, Instant now) {
        return createReturn(customerId, invoiceNumber, "Não informado", "Não informado", false, now);
    }

    public static Occurrence createReturn(
            Long customerId,
            Long invoiceNumber,
            String reason,
            String observation,
            boolean transferPossible,
            Instant now
    ) {
        return new Occurrence(
                null,
                customerId,
                invoiceNumber,
                OccurrenceType.RETURN,
                OccurrenceStatus.OPEN,
                false,
                requireNonBlank(reason, "reason"),
                requireNonBlank(observation, "observation"),
                transferPossible,
                now,
                now,
                null,
                null
        );
    }

    public static Occurrence reconstitute(
            Long id,
            Long customerId,
            Long invoiceNumber,
            OccurrenceType type,
            OccurrenceStatus status,
            boolean problemResolved,
            Instant createdAt,
            Instant updatedAt,
            Instant returnConfirmedAt,
            Instant revertedAt
    ) {
        return reconstitute(
                id, customerId, invoiceNumber, type, status, problemResolved,
                null, null, false, createdAt, updatedAt, returnConfirmedAt, revertedAt
        );
    }

    public static Occurrence reconstitute(
            Long id,
            Long customerId,
            Long invoiceNumber,
            OccurrenceType type,
            OccurrenceStatus status,
            boolean problemResolved,
            String reason,
            String observation,
            boolean transferPossible,
            Instant createdAt,
            Instant updatedAt,
            Instant returnConfirmedAt,
            Instant revertedAt
    ) {
        Objects.requireNonNull(id, "Occurrence id cannot be null when reconstituting");
        return new Occurrence(id, customerId, invoiceNumber, type, status, problemResolved,
                reason, observation, transferPossible,
                createdAt, updatedAt, returnConfirmedAt, revertedAt);
    }

    public void confirmReturn(Instant now) {
        if (type != OccurrenceType.RETURN) {
            throw new IllegalStateException("Only RETURN occurrences can be confirmed as returned");
        }
        status = OccurrenceStatus.RETURNED;
        updatedAt = Objects.requireNonNull(now, "now cannot be null");
        returnConfirmedAt = now;
    }

    public void revert(Instant now) {
        status = OccurrenceStatus.REVERTED;
        problemResolved = true;
        updatedAt = Objects.requireNonNull(now, "now cannot be null");
        revertedAt = now;
    }

    private static Long requirePositive(Long value, String field) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
        return value.trim();
    }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Long getInvoiceNumber() { return invoiceNumber; }
    public OccurrenceType getType() { return type; }
    public OccurrenceStatus getStatus() { return status; }
    public boolean isProblemResolved() { return problemResolved; }
    public String getReason() { return reason; }
    public String getObservation() { return observation; }
    public boolean isTransferPossible() { return transferPossible; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getReturnConfirmedAt() { return returnConfirmedAt; }
    public Instant getRevertedAt() { return revertedAt; }
}
