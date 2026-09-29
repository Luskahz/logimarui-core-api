package com.logimarui.journey.core;

import com.logimarui.journey.api.v1.dto.IndicatorResult;
import com.logimarui.journey.api.v1.dto.TrItem;
import com.logimarui.journey.infra.jdbc.JourneyProcedureReader;
import com.logimarui.operationalread.core.model.Expurge;
import com.logimarui.operationalread.core.model.OperationalContext;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JourneyServiceTest {
    private static final LocalDate DAY = LocalDate.of(2026, 9, 28);
    private static final LocalDateTime SNAPSHOT = DAY.atTime(18, 0);

    @Test
    void validatesPeriodModesAndReadFilters() {
        assertThatThrownBy(() -> query(DAY, DAY.minusDays(1), null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> query(null, DAY, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> query(DAY, DAY, "unknown", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> query(DAY, DAY, null, null, null, "other", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> query(DAY, DAY, null, null, null, null, "unknown"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> query(DAY, DAY, null, 0L, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void keepsMapCrewAndRechargeRowsWithoutDeduplicationOrImplicitExpurgeRemoval() {
        JourneyProcedureReader reader = mock(JourneyProcedureReader.class);
        var driver = tr(10L, 100L, "motorista", false);
        var helper = tr(10L, 200L, "ajudante", true);
        var secondMap = tr(11L, 100L, "motorista", false);
        when(reader.tr(DAY, DAY)).thenReturn(List.of(driver, helper, secondMap));
        JourneyService service = new JourneyService(reader);

        var all = service.tr(query(DAY, DAY, null, null, null, null, null));
        assertThat(all.items()).containsExactly(driver, helper, secondMap);
        assertThat(all.snapshotAt()).isEqualTo(SNAPSHOT);
        assertThat(service.tr(query(DAY, DAY, null, 10L, null, null, "expurged")).items())
                .containsExactly(helper);
        assertThat(service.tr(query(DAY, DAY, null, null, 100L, "motorista", "not_expurged")).items())
                .containsExactly(driver, secondMap);
        verify(reader, org.mockito.Mockito.times(3)).tr(DAY, DAY);
    }

    @Test
    void openCycleKeepsTargetSeparateAndOutcomeNullable() {
        TrItem item = tr(10L, 100L, "motorista", false);
        assertThat(item.tr().lifecycleStatus()).isEqualTo("EM_ANDAMENTO");
        assertThat(item.tr().targetStatus()).isEqualTo("ESTOURADO");
        assertThat(item.tr().achieved()).isNull();
        assertThat(item.tr().seconds()).isEqualTo(90001L);
        assertThat(item.mapReturnAt()).isNull();
    }

    private static JourneyQuery query(LocalDate from, LocalDate to, String mode, Long map,
                                      Long employeeCode, String role, String expurge) {
        return new JourneyQuery(from, to, mode, map, employeeCode, role, expurge);
    }

    private static TrItem tr(Long map, Long employeeCode, String role, boolean expurged) {
        return new TrItem(
                new OperationalContext(DAY, map, "LIVE", null, employeeCode, role,
                        "Crew member", 1L, "ABC", "F", 100L, null, null),
                DAY.atTime(7, 0), null, SNAPSHOT, 33600L,
                new IndicatorResult(56401L, 90001L, 33600L, "EM_ANDAMENTO", "ESTOURADO", null, false),
                null, new Expurge(expurged, expurged ? 1L : 0L, null, null, null, null,
                        new Expurge.Flags(false, false, map == 11L, false, false))
        );
    }
}
