package com.logimarui.occurrence.infra.jpa.mapper;

import com.logimarui.occurrence.core.domain.model.Occurrence;
import com.logimarui.occurrence.infra.jpa.entity.OccurrenceEntity;
import org.springframework.stereotype.Component;

@Component
public class OccurrencePersistenceMapper {
    public Occurrence toDomain(OccurrenceEntity entity) {
        return Occurrence.reconstitute(
                entity.getId(), entity.getCustomerId(), entity.getInvoiceNumber(),
                entity.getType(), entity.getStatus(), entity.isProblemResolved(),
                entity.getReason(), entity.getObservation(), entity.isTransferPossible(),
                entity.getCreatedAt(), entity.getUpdatedAt(),
                entity.getReturnConfirmedAt(), entity.getRevertedAt()
        );
    }

    public OccurrenceEntity toEntity(Occurrence occurrence) {
        return new OccurrenceEntity(
                occurrence.getId(), occurrence.getCustomerId(), occurrence.getInvoiceNumber(),
                occurrence.getType(), occurrence.getStatus(), occurrence.isProblemResolved(),
                occurrence.getReason(), occurrence.getObservation(), occurrence.isTransferPossible(),
                occurrence.getCreatedAt(), occurrence.getUpdatedAt(),
                occurrence.getReturnConfirmedAt(), occurrence.getRevertedAt()
        );
    }
}
