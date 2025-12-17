package com.logimarui.core.api.repository.read.jdbc.motorista;

import com.logimarui.core.api.domain.read.Motorista;
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
                rs.getString("cnh"),
                rs.getString("cluster")
        );
    }
}
