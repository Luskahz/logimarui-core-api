package com.logimarui.journey.infra.jdbc;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JourneyProcedureReaderTest {
    @Test
    void mapsTmlWithoutReplacingMissingPointOrOpenCycleOutcome() throws Exception {
        Map<String, Object> fields = base();
        fields.put("tipo_calculo", "ponto");
        fields.put("status_ciclo_tml", "EM_ANDAMENTO");
        fields.put("status_meta_tml", "ESTOURADO");
        fields.put("tml_segundos", 90000L);
        fields.put("tempo_check_carga_total_segundos", new BigDecimal("86500"));
        fields.put("qtd_check_carga", BigDecimal.ONE);
        fields.put("anomalia_ordem_temporal_tml", 1);
        fields.put("expurgo_recarga", 1);
        var item = JourneyProcedureReader.mapTml(row(fields));
        assertThat(item.context().map()).isEqualTo(10L);
        assertThat(item.pointOrigin()).isNull();
        assertThat(item.expurge().flags().recharge()).isTrue();
        assertThat(item.tml().seconds()).isEqualTo(90000L);
        assertThat(item.tml().achieved()).isNull();
        assertThat(item.tml().lifecycleStatus()).isEqualTo("EM_ANDAMENTO");
        assertThat(item.tml().targetStatus()).isEqualTo("ESTOURADO");
        assertThat(item.tml().temporalOrderAnomaly()).isTrue();
        assertThat(item.loadChecklist().totalSeconds()).isEqualTo(86500L);
    }

    @Test
    void mapsTrAsMapFactOnCrewRowWithNumericDuration() throws Exception {
        Map<String, Object> fields = base();
        fields.put("tr_segundos", 100000L);
        fields.put("tempo_previsto_segundos", 35000L);
        fields.put("dispersao_tr", new BigDecimal("1.25"));
        fields.put("atingimento_tr", null);
        var item = JourneyProcedureReader.mapTr(row(fields));
        assertThat(item.context().employeeCode()).isEqualTo(100L);
        assertThat(item.tr().seconds()).isEqualTo(100000L);
        assertThat(item.plannedSeconds()).isEqualTo(35000L);
        assertThat(item.dispersion()).isEqualByComparingTo("1.25");
        assertThat(item.tr().achieved()).isNull();
    }

    @Test
    void mapsTiStagesAndLongSourceDurations() throws Exception {
        Map<String, Object> fields = base();
        fields.put("tipo_calculo", "mpd");
        fields.put("etapa_operacional_ti", "SEM_PFIN");
        fields.put("pfis_segundos", 601);
        fields.put("tempo_interno_origem_segundos", 90001L);
        fields.put("dt_entrada_veiculo", Timestamp.valueOf(LocalDateTime.of(2026, 9, 28, 20, 0)));
        var item = JourneyProcedureReader.mapTi(row(fields));
        assertThat(item.mode()).isEqualTo("mpd");
        assertThat(item.operationalStage()).isEqualTo("SEM_PFIN");
        assertThat(item.physicalClose().seconds()).isEqualTo(601L);
        assertThat(item.sourceInternalSeconds()).isEqualTo(90001L);
        assertThat(item.vehicleEntryAt()).isEqualTo(LocalDateTime.of(2026, 9, 28, 20, 0));
    }

    @Test
    void mapsJlComponentsWithoutInventingComponentLifecycleOrSummingTwice() throws Exception {
        Map<String, Object> fields = base();
        fields.put("tipo_calculo", "ponto");
        fields.put("data_jornada_ponto", Date.valueOf("2026-09-28"));
        fields.put("jl_segundos", 90001L);
        fields.put("soma_componentes_jl_segundos", 40000L);
        fields.put("divergencia_jl_segundos", -2L);
        fields.put("status_ciclo_jl", "FINALIZADO");
        fields.put("atingimento_jl", 0);
        var item = JourneyProcedureReader.mapJl(row(fields));
        assertThat(item.pointJourneyDate()).isEqualTo(LocalDate.of(2026, 9, 28));
        assertThat(item.jl().seconds()).isEqualTo(90001L);
        assertThat(item.jl().achieved()).isFalse();
        assertThat(item.tml().lifecycleStatus()).isNull();
        assertThat(item.componentSumSeconds()).isEqualTo(40000L);
        assertThat(item.divergenceSeconds()).isEqualTo(-2L);
    }

    @Test
    void failsOnMissingPhysicalColumnInsteadOfSilentlyUsingAnotherName() throws Exception {
        ResultSet row = row(base());
        when(row.getObject("origem_mapa")).thenThrow(new SQLException("Unknown column origem_mapa"));
        assertThatThrownBy(() -> JourneyProcedureReader.mapTr(row))
                .isInstanceOf(SQLException.class).hasMessageContaining("origem_mapa");
    }

    private static Map<String, Object> base() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", Date.valueOf("2026-09-28"));
        result.put("mapa", 10);
        result.put("origem_mapa", "LIVE");
        result.put("codigo_colaborador", 100);
        result.put("funcao", "motorista");
        result.put("veiculo", 1);
        result.put("dt_snapshot", Timestamp.valueOf("2026-09-28 20:00:00"));
        result.put("possui_expurgo_tml", 0);
        result.put("possui_expurgo_tr", 0);
        result.put("possui_expurgo_ti", 0);
        result.put("possui_expurgo_jl", 0);
        return result;
    }

    private static ResultSet row(Map<String, Object> fields) throws SQLException {
        ResultSet result = mock(ResultSet.class);
        when(result.getObject(anyString())).thenAnswer(call -> fields.get(call.getArgument(0, String.class)));
        return result;
    }
}
