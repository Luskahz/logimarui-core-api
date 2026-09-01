package com.logimarui.occurrence.api.v2.mapper;

import com.logimarui.occurrence.api.v2.dto.OccurrenceResponse;
import com.logimarui.occurrence.core.domain.model.Occurrence;
import org.springframework.stereotype.Component;

@Component
public class OccurrenceApiMapper {
    public OccurrenceResponse toResponse(Occurrence occurrence) {
        return new OccurrenceResponse(
                occurrence.getId(), occurrence.getCustomerId(), occurrence.getInvoiceNumber(),
                occurrence.getType(), occurrence.getStatus(), occurrence.isProblemResolved(),
                occurrence.getReason(), occurrence.getObservation(), occurrence.isTransferPossible(),
                occurrence.getCreatedAt(), occurrence.getUpdatedAt(),
                occurrence.getReturnConfirmedAt(), occurrence.getRevertedAt()
        );
    }
}
