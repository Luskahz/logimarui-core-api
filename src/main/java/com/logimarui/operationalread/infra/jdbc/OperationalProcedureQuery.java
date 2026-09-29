package com.logimarui.operationalread.infra.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Executes one approved READ-side procedure and consumes its first result set. */
@Component
public class OperationalProcedureQuery {
    private final JdbcTemplate jdbcTemplate;

    public OperationalProcedureQuery(@Qualifier("clusterProcedureJdbcTemplate") JdbcTemplate jdbcTemplate) {
        // This existing pool shares READ credentials but permits the procedures' temporary-table writes.
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> List<T> read(String procedure, LocalDate from, LocalDate to, String mode,
                            RowMapper<T> mapper) {
        if (procedure == null || !procedure.matches("[a-z][a-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid procedure name");
        }
        String call = mode == null ? "{call " + procedure + "(?, ?)}" : "{call " + procedure + "(?, ?, ?)}";
        return jdbcTemplate.execute((ConnectionCallback<List<T>>) connection -> {
            try (CallableStatement statement = connection.prepareCall(call)) {
                statement.setDate(1, Date.valueOf(from));
                statement.setDate(2, Date.valueOf(to));
                if (mode != null) statement.setString(3, mode);
                boolean hasResultSet = statement.execute();
                while (hasResultSet || statement.getUpdateCount() != -1) {
                    if (hasResultSet) {
                        try (ResultSet rows = statement.getResultSet()) {
                            if (isOperationalRows(rows)) {
                                List<T> items = new ArrayList<>();
                                int index = 0;
                                while (rows.next()) items.add(mapper.mapRow(rows, index++));
                                return items;
                            }
                        }
                    }
                    hasResultSet = statement.getMoreResults();
                }
                throw new DataRetrievalFailureException("No result set from " + procedure);
            } catch (SQLException exception) {
                throw jdbcTemplate.getExceptionTranslator().translate("call " + procedure, call, exception);
            }
        });
    }

    private boolean isOperationalRows(ResultSet rows) throws SQLException {
        ResultSetMetaData metadata = rows.getMetaData();
        boolean date = false;
        boolean map = false;
        boolean employee = false;
        for (int column = 1; column <= metadata.getColumnCount(); column++) {
            String label = metadata.getColumnLabel(column);
            date |= "data".equalsIgnoreCase(label);
            map |= "mapa".equalsIgnoreCase(label);
            employee |= "codigo_colaborador".equalsIgnoreCase(label);
        }
        return date && map && employee;
    }
}
