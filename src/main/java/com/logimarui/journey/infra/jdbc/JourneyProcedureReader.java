package com.logimarui.journey.infra.jdbc;

import com.logimarui.journey.core.model.IndicatorResult;
import com.logimarui.journey.core.model.JlItem;
import com.logimarui.journey.core.model.TiItem;
import com.logimarui.journey.core.model.TmlItem;
import com.logimarui.journey.core.model.TrItem;
import com.logimarui.journey.core.port.JourneyReadRepository;
import com.logimarui.operationalread.infra.jdbc.OperationalProcedureQuery;
import com.logimarui.operationalread.infra.jdbc.ResultColumns;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.logimarui.operationalread.infra.jdbc.ResultColumns.*;

/** One stored-procedure call per HTTP request, without Java KPI recalculation. */
@Repository
public class JourneyProcedureReader implements JourneyReadRepository {
    private final OperationalProcedureQuery procedureQuery;

    public JourneyProcedureReader(OperationalProcedureQuery procedureQuery) {
        this.procedureQuery = procedureQuery;
    }

    public List<TmlItem> tml(LocalDate from, LocalDate to, String mode) {
        return procedureQuery.read(JourneyProcedure.TML.spec(), from, to, mode, (row, index) -> mapTml(row));
    }

    public List<TrItem> tr(LocalDate from, LocalDate to) {
        return procedureQuery.read(JourneyProcedure.TR.spec(), from, to, null, (row, index) -> mapTr(row));
    }

    public List<TiItem> ti(LocalDate from, LocalDate to, String mode) {
        return procedureQuery.read(JourneyProcedure.TI.spec(), from, to, mode, (row, index) -> mapTi(row));
    }

    public List<JlItem> jl(LocalDate from, LocalDate to, String mode) {
        return procedureQuery.read(JourneyProcedure.JL.spec(), from, to, mode, (row, index) -> mapJl(row));
    }

    public static TmlItem mapTml(ResultSet row) throws SQLException {
        return new TmlItem(
                context(row), string(row, "tipo_calculo"), string(row, "origem_ponto"),
                dateTime(row, "dt_entrada"), dateTime(row, "dt_matinal"),
                dateTime(row, "dt_saida_mapa"), dateTime(row, "dt_inicio_tml"),
                dateTime(row, "dt_fim_tml"), dateTime(row, "dt_snapshot"),
                metric(row, "tml", true),
                new TmlItem.Checklist(
                        seconds(row, "qtd_check_carga"), seconds(row, "tempo_check_carga_total_segundos"),
                        seconds(row, "tempo_check_carga_efetivo_segundos"),
                        dateTime(row, "dt_inicio_check_carga_efetivo"),
                        dateTime(row, "dt_fim_check_carga_efetivo"), bool(row, "check_carga_aderente_tml")
                ),
                new TmlItem.Checklist(
                        seconds(row, "qtd_check_manutencao"),
                        seconds(row, "tempo_check_manutencao_total_segundos"),
                        seconds(row, "tempo_check_manutencao_efetivo_segundos"),
                        dateTime(row, "dt_inicio_check_manutencao_efetivo"),
                        dateTime(row, "dt_fim_check_manutencao_efetivo"),
                        bool(row, "check_manutencao_aderente_tml")
                ),
                seconds(row, "tempo_checklists_total_segundos"), expurge(row, "tml")
        );
    }

    public static TrItem mapTr(ResultSet row) throws SQLException {
        return new TrItem(
                context(row), dateTime(row, "dt_saida_mapa"), dateTime(row, "dt_retorno_mapa"),
                dateTime(row, "dt_snapshot"), seconds(row, "tempo_previsto_segundos"),
                metric(row, "tr", true), decimal(row, "dispersao_tr"), expurge(row, "tr")
        );
    }

    public static TiItem mapTi(ResultSet row) throws SQLException {
        return new TiItem(
                context(row), string(row, "tipo_calculo"), dateTime(row, "dt_saida_mapa"),
                dateTime(row, "dt_entrada_veiculo"), dateTime(row, "dt_prest_fis"),
                dateTime(row, "dt_prest_fin"), string(row, "origem_ponto"),
                dateTime(row, "dt_ponto_saida"), dateTime(row, "dt_fim_ti"),
                dateTime(row, "dt_snapshot"), string(row, "etapa_operacional_ti"),
                metric(row, "pfis", true), metric(row, "pfin", true), metric(row, "ti", true),
                seconds(row, "tempo_p_fis_origem_segundos"),
                seconds(row, "tempo_p_fin_origem_segundos"),
                seconds(row, "tempo_interno_origem_segundos"), expurge(row, "ti")
        );
    }

    public static JlItem mapJl(ResultSet row) throws SQLException {
        return new JlItem(
                context(row), string(row, "tipo_calculo"), date(row, "data_jornada_ponto"),
                string(row, "origem_ponto"), dateTime(row, "dt_matinal"),
                dateTime(row, "dt_ponto_entrada"), dateTime(row, "dt_saida_mapa"),
                dateTime(row, "dt_retorno_mapa"), dateTime(row, "dt_prest_fis"),
                dateTime(row, "dt_prest_fin"), dateTime(row, "dt_ponto_saida"),
                dateTime(row, "dt_inicio_jl"), dateTime(row, "dt_fim_jl"),
                dateTime(row, "dt_snapshot"), string(row, "etapa_operacional_jl"),
                metric(row, "tml", false), metric(row, "tr", false),
                metric(row, "pfis", false), metric(row, "pfin", false),
                metric(row, "ti", false), metric(row, "jl", true),
                seconds(row, "soma_componentes_jl_segundos"),
                seconds(row, "divergencia_jl_segundos"), expurge(row, "jl")
        );
    }

    private static IndicatorResult metric(ResultSet row, String indicator, boolean hasLifecycle)
            throws SQLException {
        return new IndicatorResult(
                seconds(row, indicator + "_segundos_diferenca"), seconds(row, indicator + "_segundos"),
                seconds(row, "meta_" + indicator + "_segundos"),
                hasLifecycle ? string(row, "status_ciclo_" + indicator) : null,
                string(row, "status_meta_" + indicator),
                bool(row, "atingimento_" + indicator),
                bool(row, "anomalia_ordem_temporal_" + indicator)
        );
    }
}
