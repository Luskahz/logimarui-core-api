package com.logimarui.occurrence.core.usecase;

import com.logimarui.occurrence.core.domain.model.Occurrence;
import com.logimarui.occurrence.core.exception.OccurrenceNotFoundException;
import com.logimarui.occurrence.core.port.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RevertOccurrenceUseCase {
    private final OccurrenceRepository occurrenceRepository;
    private final Clock clock;

    @Transactional
    public Occurrence execute(Long occurrenceId) {
        Occurrence occurrence = occurrenceRepository.findById(occurrenceId)
                .orElseThrow(() -> new OccurrenceNotFoundException(occurrenceId));
        occurrence.revert(Instant.now(clock));
        return occurrenceRepository.save(occurrence);
    }
}
