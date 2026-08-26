package com.logimarui.occurrence.core.port.repository;

import com.logimarui.occurrence.core.domain.model.Occurrence;
import com.logimarui.occurrence.core.domain.model.OccurrenceSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OccurrenceRepository {
    Occurrence save(Occurrence occurrence);
    Optional<Occurrence> findById(Long id);
    Page<Occurrence> search(OccurrenceSearchCriteria criteria, Pageable pageable);
}
