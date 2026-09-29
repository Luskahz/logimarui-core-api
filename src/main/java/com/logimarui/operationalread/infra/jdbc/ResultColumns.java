package com.logimarui.operationalread.infra.jdbc;

import com.logimarui.operationalread.core.model.Expurge;
import com.logimarui.operationalread.core.model.OperationalContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Strict conversion at the database boundary. Missing columns fail at their real name. */
public final class ResultColumns {
    private ResultColumns() {
    }

    public static String string(ResultSet row, String column) throws SQLException {
        Object value = row.getObject(column);
        return value == null ? null : value.toString();
    }

    public static Long seconds(ResultSet row, String column) throws SQLException {
        Object value = row.getObject(column);
        if (value == null) return null;
        if (value instanceof BigDecimal decimal) return decimal.longValueExact();
        if (value instanceof Number number) return number.longValue();
        throw new SQLException("Expected numeric seconds in column " + column + ", got " + value.getClass().getName());
    }

    public static Boolean bool(ResultSet row, String column) throws SQLException {
        Object value = row.getObject(column);
        if (value == null) return null;
        if (value instanceof Boolean booleanValue) return booleanValue;
        if (value instanceof Number number) {
            int integer = number.intValue();
            if (integer == 0 || integer == 1) return integer == 1;
        }
        throw new SQLException("Expected nullable 0/1 in column " + column);
    }

    public static LocalDate date(ResultSet row, String column) throws SQLException {
        Object value = row.getObject(column);
        if (value == null) return null;
        if (value instanceof LocalDate localDate) return localDate;
        if (value instanceof Date sqlDate) return sqlDate.toLocalDate();
        throw new SQLException("Expected DATE in column " + column);
    }

    public static LocalDateTime dateTime(ResultSet row, String column) throws SQLException {
        Object value = row.getObject(column);
        if (value == null) return null;
        if (value instanceof LocalDateTime localDateTime) return localDateTime;
        if (value instanceof Timestamp timestamp) return timestamp.toLocalDateTime();
        throw new SQLException("Expected DATETIME in column " + column);
    }

    public static BigDecimal decimal(ResultSet row, String column) throws SQLException {
        Object value = row.getObject(column);
        if (value == null) return null;
        if (value instanceof BigDecimal decimal) return decimal;
        if (value instanceof Number number) return new BigDecimal(number.toString());
        throw new SQLException("Expected DECIMAL in column " + column);
    }

    public static OperationalContext context(ResultSet row) throws SQLException {
        return new OperationalContext(
                date(row, "data"), seconds(row, "mapa"), string(row, "origem_mapa"),
                seconds(row, "matricula"), seconds(row, "codigo_colaborador"),
                string(row, "funcao"), string(row, "nome_colaborador"),
                seconds(row, "veiculo"), string(row, "placa"), string(row, "frota"),
                seconds(row, "codigo_motorista_mapa"), seconds(row, "superv_rota"),
                string(row, "nome_superv_rota")
        );
    }

    public static Expurge expurge(ResultSet row, String indicator) throws SQLException {
        return new Expurge(
                bool(row, "possui_expurgo_" + indicator),
                seconds(row, "qtd_expurgos_" + indicator),
                string(row, "ids_expurgos_" + indicator),
                string(row, "tipos_expurgo_" + indicator),
                string(row, "motivos_expurgos_" + indicator),
                string(row, "observacoes_expurgos_" + indicator),
                new Expurge.Flags(
                        bool(row, "expurgo_mapa_parado"), bool(row, "expurgo_considerar_rv"),
                        bool(row, "expurgo_recarga"), bool(row, "expurgo_historico"),
                        bool(row, "expurgo_geral")
                )
        );
    }
}
