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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Executes a parametrized READ-side procedure using its caller-supplied result signature. */
@Component
public class OperationalProcedureQuery {
    private final JdbcTemplate jdbcTemplate;

    public OperationalProcedureQuery(@Qualifier("clusterProcedureJdbcTemplate") JdbcTemplate jdbcTemplate) {
        // This existing pool shares READ credentials but permits the procedures' temporary-table writes.
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> List<T> read(ProcedureSpec procedure, LocalDate from, LocalDate to, String mode,
                            RowMapper<T> mapper) {
        if (procedure == null) throw new IllegalArgumentException("Procedure is required");
        procedure.validateMode(mode);
        String call = mode == null ? "{call " + procedure.sqlName() + "(?, ?)}"
                : "{call " + procedure.sqlName() + "(?, ?, ?)}";
        return jdbcTemplate.execute((ConnectionCallback<List<T>>) connection -> {
            try (CallableStatement statement = connection.prepareCall(call)) {
                statement.setDate(1, Date.valueOf(from));
                statement.setDate(2, Date.valueOf(to));
                if (mode != null) statement.setString(3, mode);
                boolean hasResultSet = statement.execute();
                while (hasResultSet || statement.getUpdateCount() != -1) {
                    if (hasResultSet) {
                        try (ResultSet rows = statement.getResultSet()) {
                            if (isOperationalRows(rows, procedure)) {
                                List<T> items = new ArrayList<>();
                                int index = 0;
                                while (rows.next()) items.add(mapper.mapRow(rows, index++));
                                return items;
                            }
                        }
                    }
                    hasResultSet = statement.getMoreResults();
                }
                throw new DataRetrievalFailureException("No operational result set from " + procedure.sqlName());
            } catch (SQLException exception) {
                throw jdbcTemplate.getExceptionTranslator().translate("call " + procedure.sqlName(), call, exception);
            }
        });
    }

    private boolean isOperationalRows(ResultSet rows, ProcedureSpec procedure) throws SQLException {
        ResultSetMetaData metadata = rows.getMetaData();
        Set<String> columns = new HashSet<>();
        for (int column = 1; column <= metadata.getColumnCount(); column++) {
            columns.add(metadata.getColumnLabel(column).toLowerCase(Locale.ROOT));
        }
        return columns.containsAll(procedure.requiredColumns());
    }
}
