package com.logimarui.all.core.api.repository.read.jdbc.supervisor;

import com.logimarui.all.core.api.domain.read.Supervisor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SupervisorRowMapper implements RowMapper<Supervisor> {


    @Override
    public Supervisor mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new Supervisor(
                rs.getLong("codigo"),
                rs.getString("nome"),
                rs.getTime("matinal").toLocalTime()
        );
    }
}
