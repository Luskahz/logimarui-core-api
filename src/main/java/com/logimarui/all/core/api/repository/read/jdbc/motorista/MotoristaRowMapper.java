package com.logimarui.all.core.api.repository.read.jdbc.motorista;

import com.logimarui.all.core.api.domain.read.Motorista;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MotoristaRowMapper implements RowMapper<Motorista> {

    @Override
    public Motorista mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new Motorista(
                rs.getLong("codigo"),
                rs.getLong("matricula"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("cluster")
        );
    }
}
