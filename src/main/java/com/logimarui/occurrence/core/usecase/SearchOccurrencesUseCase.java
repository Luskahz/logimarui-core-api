package com.logimarui.occurrence.core.usecase;

import com.logimarui.occurrence.core.domain.model.Occurrence;
import com.logimarui.occurrence.core.domain.model.OccurrenceSearchCriteria;
import com.logimarui.occurrence.core.port.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchOccurrencesUseCase {
    private final OccurrenceRepository occurrenceRepository;

    @Transactional(readOnly = true)
    public Page<Occurrence> execute(OccurrenceSearchCriteria criteria, Pageable pageable) {
        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size must not exceed 100");
        }
        return occurrenceRepository.search(criteria, pageable);
    }
}
