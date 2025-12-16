package com.logimarui.core.api.repository.read.jdbc.cliente;

import com.logimarui.core.api.domain.read.Cliente;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteRowMapper implements RowMapper<Cliente> {

    @Override
    public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException {
        Cliente c = new Cliente();
        c.setCodigo(rs.getLong("codigo"));
        c.setCpf(rs.getString("cpf"));
        c.setCnpj(rs.getString("cnpj"));
        c.setFantasia(rs.getString("fantasia"));
        c.setRazaoSocial(rs.getString("razao_social"));
        c.setLatitude(rs.getBigDecimal("latitude"));
        c.setLongitude(rs.getBigDecimal("longitude"));
        return c;
    }
}
