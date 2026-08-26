package com.logimarui.occurrence.core.usecase;

import com.logimarui.occurrence.core.domain.model.Occurrence;
import com.logimarui.occurrence.core.exception.OccurrenceNotFoundException;
import com.logimarui.occurrence.core.exception.OccurrenceStateConflictException;
import com.logimarui.occurrence.core.port.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConfirmReturnUseCase {
    private final OccurrenceRepository occurrenceRepository;
    private final Clock clock;

    @Transactional
    public Occurrence execute(Long occurrenceId) {
        Occurrence occurrence = occurrenceRepository.findById(occurrenceId)
                .orElseThrow(() -> new OccurrenceNotFoundException(occurrenceId));
        try {
            occurrence.confirmReturn(Instant.now(clock));
        } catch (IllegalStateException exception) {
            throw new OccurrenceStateConflictException(exception.getMessage());
        }
        return occurrenceRepository.save(occurrence);
    }
}
