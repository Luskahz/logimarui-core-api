package com.logimarui.operationalread.infra.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataRetrievalFailureException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OperationalProcedureQueryTest {
    @Test
    @SuppressWarnings("unchecked")
    void rejectsAnotherIndicatorsResultSetEvenWithSharedIdentityColumns() throws Exception {
        JdbcTemplate template = mock(JdbcTemplate.class);
        Connection connection = mock(Connection.class);
        CallableStatement statement = mock(CallableStatement.class);
        ResultSet rows = mock(ResultSet.class);
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(connection.prepareCall("{call sp_tml_v2(?, ?, ?)}")).thenReturn(statement);
        when(statement.execute()).thenReturn(true);
        when(statement.getResultSet()).thenReturn(rows);
        when(statement.getMoreResults()).thenReturn(false);
        when(statement.getUpdateCount()).thenReturn(-1);
        when(rows.getMetaData()).thenReturn(metadata);
        when(metadata.getColumnCount()).thenReturn(4);
        when(metadata.getColumnLabel(1)).thenReturn("data");
        when(metadata.getColumnLabel(2)).thenReturn("mapa");
        when(metadata.getColumnLabel(3)).thenReturn("codigo_colaborador");
        when(metadata.getColumnLabel(4)).thenReturn("tr_segundos");
        when(template.execute(any(ConnectionCallback.class))).thenAnswer(invocation ->
                ((ConnectionCallback<List<String>>) invocation.getArgument(0)).doInConnection(connection));

        assertThatThrownBy(() -> new OperationalProcedureQuery(template).read(
                ApprovedOperationalProcedure.TML, LocalDate.of(2026, 9, 28),
                LocalDate.of(2026, 9, 28), "ponto", (row, index) -> "wrong"))
                .isInstanceOf(DataRetrievalFailureException.class);
        verify(rows).close();
        verify(statement).close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void skipsAnEarlierInformationalResultSet() throws Exception {
        JdbcTemplate template = mock(JdbcTemplate.class);
        Connection connection = mock(Connection.class);
        CallableStatement statement = mock(CallableStatement.class);
        ResultSet info = mock(ResultSet.class);
        ResultSet operational = mock(ResultSet.class);
        ResultSetMetaData infoMetadata = mock(ResultSetMetaData.class);
        ResultSetMetaData operationalMetadata = mock(ResultSetMetaData.class);
        when(connection.prepareCall("{call sp_tr_v2(?, ?)}")).thenReturn(statement);
        when(statement.execute()).thenReturn(true);
        when(statement.getResultSet()).thenReturn(info, operational);
        when(statement.getMoreResults()).thenReturn(true);
        when(info.getMetaData()).thenReturn(infoMetadata);
        when(infoMetadata.getColumnCount()).thenReturn(1);
        when(infoMetadata.getColumnLabel(1)).thenReturn("message");
        when(operational.getMetaData()).thenReturn(operationalMetadata);
        when(operationalMetadata.getColumnCount()).thenReturn(4);
        when(operationalMetadata.getColumnLabel(1)).thenReturn("data");
        when(operationalMetadata.getColumnLabel(2)).thenReturn("mapa");
        when(operationalMetadata.getColumnLabel(3)).thenReturn("codigo_colaborador");
        when(operationalMetadata.getColumnLabel(4)).thenReturn("tr_segundos");
        when(operational.next()).thenReturn(true, false);
        when(template.execute(any(ConnectionCallback.class))).thenAnswer(invocation ->
                ((ConnectionCallback<List<String>>) invocation.getArgument(0)).doInConnection(connection));

        var result = new OperationalProcedureQuery(template).read(ApprovedOperationalProcedure.TR,
                LocalDate.of(2026, 9, 28), LocalDate.of(2026, 9, 28), null,
                (row, index) -> "operational");
        assertThat(result).containsExactly("operational");
        verify(info).close();
        verify(operational).close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void callsApprovedProcedureOnceAndClosesStatementAndRows() throws Exception {
        JdbcTemplate template = mock(JdbcTemplate.class);
        Connection connection = mock(Connection.class);
        CallableStatement statement = mock(CallableStatement.class);
        ResultSet rows = mock(ResultSet.class);
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        LocalDate day = LocalDate.of(2026, 9, 28);
        when(connection.prepareCall("{call sp_tml_v2(?, ?, ?)}" )).thenReturn(statement);
        when(statement.execute()).thenReturn(true);
        when(statement.getResultSet()).thenReturn(rows);
        when(rows.getMetaData()).thenReturn(metadata);
        when(metadata.getColumnCount()).thenReturn(4);
        when(metadata.getColumnLabel(1)).thenReturn("data");
        when(metadata.getColumnLabel(2)).thenReturn("mapa");
        when(metadata.getColumnLabel(3)).thenReturn("codigo_colaborador");
        when(metadata.getColumnLabel(4)).thenReturn("tml_segundos");
        when(rows.next()).thenReturn(true, true, false);
        when(template.execute(any(ConnectionCallback.class))).thenAnswer(invocation ->
                ((ConnectionCallback<List<String>>) invocation.getArgument(0)).doInConnection(connection));

        List<String> result = new OperationalProcedureQuery(template)
                .read(ApprovedOperationalProcedure.TML, day, day, "ponto", (row, index) -> "row" + index);

        assertThat(result).containsExactly("row0", "row1");
        verify(statement).setDate(1, Date.valueOf(day));
        verify(statement).setDate(2, Date.valueOf(day));
        verify(statement).setString(3, "ponto");
        verify(statement).execute();
        verify(rows).close();
        verify(statement).close();
        assertThatThrownBy(() -> new OperationalProcedureQuery(template)
                .read(null, day, day, "ponto", (row, index) -> "x"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new OperationalProcedureQuery(template)
                .read(ApprovedOperationalProcedure.TR, day, day, "ponto", (row, index) -> "x"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
