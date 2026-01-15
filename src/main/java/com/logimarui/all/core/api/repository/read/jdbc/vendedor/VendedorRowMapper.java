package com.logimarui.all.core.api.repository.read.jdbc.vendedor;

import com.logimarui.all.core.api.domain.read.Vendedor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VendedorRowMapper implements RowMapper<Vendedor> {
    @Override
    public Vendedor mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new Vendedor(
                rs.getLong("codigo"),
                rs.getString("nome"),
                rs.getInt("area")
        );
    }
}
