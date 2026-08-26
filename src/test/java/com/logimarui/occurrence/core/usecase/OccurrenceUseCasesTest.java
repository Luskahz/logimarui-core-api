package com.logimarui.occurrence.core.usecase;

import com.logimarui.occurrence.core.domain.enums.OccurrenceStatus;
import com.logimarui.occurrence.core.domain.enums.OccurrenceType;
import com.logimarui.occurrence.core.domain.model.Occurrence;
import com.logimarui.occurrence.core.domain.model.OccurrenceSearchCriteria;
import com.logimarui.occurrence.core.port.repository.OccurrenceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OccurrenceUseCasesTest {
    private static final Instant NOW = Instant.parse("2026-08-26T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void createsOpenReturnOccurrence() {
        OccurrenceRepository repository = mock(OccurrenceRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Occurrence result = new CreateReturnOccurrenceUseCase(repository, CLOCK).execute(10L, 20L);

        assertThat(result.getType()).isEqualTo(OccurrenceType.RETURN);
        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.OPEN);
        assertThat(result.isProblemResolved()).isFalse();
        verify(repository).save(result);
    }

    @Test
    void confirmsReturnAndPersistsTimestamp() {
        OccurrenceRepository repository = mock(OccurrenceRepository.class);
        Occurrence occurrence = openOccurrence();
        when(repository.findById(1L)).thenReturn(Optional.of(occurrence));
        when(repository.save(occurrence)).thenReturn(occurrence);

        Occurrence result = new ConfirmReturnUseCase(repository, CLOCK).execute(1L);

        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.RETURNED);
        assertThat(result.getReturnConfirmedAt()).isEqualTo(NOW);
        verify(repository).save(occurrence);
    }

    @Test
    void revertsAndMarksProblemAsResolved() {
        OccurrenceRepository repository = mock(OccurrenceRepository.class);
        Occurrence occurrence = openOccurrence();
        when(repository.findById(1L)).thenReturn(Optional.of(occurrence));
        when(repository.save(occurrence)).thenReturn(occurrence);

        Occurrence result = new RevertOccurrenceUseCase(repository, CLOCK).execute(1L);

        assertThat(result.getStatus()).isEqualTo(OccurrenceStatus.REVERTED);
        assertThat(result.isProblemResolved()).isTrue();
        assertThat(result.getRevertedAt()).isEqualTo(NOW);
    }

    @Test
    void delegatesCombinedFiltersAndPageableToRepository() {
        OccurrenceRepository repository = mock(OccurrenceRepository.class);
        var criteria = new OccurrenceSearchCriteria(
                10L, 20L, OccurrenceType.RETURN, OccurrenceStatus.OPEN, false
        );
        var pageable = PageRequest.of(0, 20);
        var expected = new PageImpl<>(List.of(openOccurrence()), pageable, 1);
        when(repository.search(criteria, pageable)).thenReturn(expected);

        var result = new SearchOccurrencesUseCase(repository).execute(criteria, pageable);

        assertThat(result).isSameAs(expected);
        verify(repository).search(criteria, pageable);
    }

    private Occurrence openOccurrence() {
        return Occurrence.reconstitute(
                1L, 10L, 20L, OccurrenceType.RETURN, OccurrenceStatus.OPEN,
                false, NOW.minusSeconds(60), NOW.minusSeconds(60), null, null
        );
    }
}
