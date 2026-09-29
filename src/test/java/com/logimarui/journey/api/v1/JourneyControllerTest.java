package com.logimarui.journey.api.v1;

import com.logimarui.journey.core.model.JourneyPeriod;
import com.logimarui.journey.core.model.IndicatorResult;
import com.logimarui.journey.core.model.TmlItem;
import com.logimarui.journey.core.JourneyService;
import com.logimarui.operationalread.core.model.Expurge;
import com.logimarui.operationalread.core.model.OperationalContext;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JourneyControllerTest {
    private final JourneyService service = mock(JourneyService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new JourneyController(service))
            .setControllerAdvice(new JourneyExceptionHandler()).build();

    @Test
    void exposesFourVersionedSnapshotRoutes() throws Exception {
        when(service.tml(any())).thenReturn(empty());
        when(service.tr(any())).thenReturn(empty());
        when(service.ti(any())).thenReturn(empty());
        when(service.jl(any())).thenReturn(empty());
        for (String path : List.of("tml", "tr", "ti", "jl")) {
            var request = get("/api/v1/journey/" + path)
                    .param("from", "2026-09-01").param("to", "2026-09-30");
            if (!path.equals("tr")) request.param("mode", "ponto");
            mvc.perform(request).andExpect(status().isOk())
                    .andExpect(jsonPath("$.from").value("2026-09-01"))
                    .andExpect(jsonPath("$.items").isArray());
        }
    }

    @Test
    void rejectsMissingDatesInvalidModeAndReversePeriod() throws Exception {
        mvc.perform(get("/api/v1/journey/tr").param("to", "2026-09-30"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/journey/tml")
                        .param("from", "2026-09-01").param("to", "2026-09-30"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/journey/tr")
                        .param("from", "2026-09-30").param("to", "2026-09-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_JOURNEY_REQUEST"));
        mvc.perform(get("/api/v1/journey/ti")
                        .param("from", "2026-09-01").param("to", "2026-09-30")
                        .param("mode", "invalid"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/journey/tr")
                        .param("from", "2026-09-01").param("to", "2026-09-30")
                        .param("mode", "ponto"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void treatsJdbcFailureAsSafeApiError() throws Exception {
        when(service.tr(any())).thenThrow(new DataAccessResourceFailureException("Database secret detail"));
        mvc.perform(get("/api/v1/journey/tr")
                        .param("from", "2026-09-01").param("to", "2026-09-30"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("JOURNEY_READ_ERROR"))
                .andExpect(jsonPath("$.message").value("Unable to read journey data"));
    }

    @Test
    void serializesNullOutcomeAndSecondsOverTwentyFourHours() throws Exception {
        LocalDate day = LocalDate.of(2026, 9, 28);
        var item = new TmlItem(
                new OperationalContext(day, 10L, "LIVE", null, 100L, "motorista",
                        "Driver", 1L, null, null, 100L, null, null),
                "ponto", null, null, null, null, null, null, null,
                new IndicatorResult(88200L, 90000L, 1800L,
                        "EM_ANDAMENTO", "ESTOURADO", null, false),
                null, null, null,
                new Expurge(false, 0L, null, null, null, null,
                        new Expurge.Flags(false, false, false, false, false)));
        when(service.tml(any())).thenReturn(new JourneyPeriod<>(day, day, null, List.of(item)));
        mvc.perform(get("/api/v1/journey/tml")
                        .param("from", "2026-09-28").param("to", "2026-09-28")
                        .param("mode", "ponto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].context.map").value(10))
                .andExpect(jsonPath("$.items[0].tml.seconds").value(90000))
                .andExpect(jsonPath("$.items[0].tml.lifecycleStatus").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.items[0].tml.targetStatus").value("ESTOURADO"))
                .andExpect(jsonPath("$.items[0].tml.achieved").value(nullValue()));
    }

    private static <T> JourneyPeriod<T> empty() {
        return new JourneyPeriod<>(LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 30), null, List.of());
    }
}
