package com.logimarui.core.api.repository.read.jdbc.cliente;

import com.logimarui.core.api.domain.read.Cliente;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteRowMapper implements RowMapper<Cliente> {

    @Override
    public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Cliente(
                rs.getLong("codigo"),
                rs.getString("fantasia"),
                rs.getString("cpf"),
                rs.getString("cnpj"),
                rs.getString("razao_social"),
                rs.getBigDecimal("latitude"),
                rs.getBigDecimal("longitude"));
    }
}
