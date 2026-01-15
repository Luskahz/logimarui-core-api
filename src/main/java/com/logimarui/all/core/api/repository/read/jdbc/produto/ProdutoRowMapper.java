package com.logimarui.all.core.api.repository.read.jdbc.produto;

import com.logimarui.all.core.api.domain.read.Produto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProdutoRowMapper  implements RowMapper<Produto> {

    @Override
    public Produto mapRow(ResultSet rs, int rowNum) throws SQLException{
        return new Produto(
                rs.getLong("codigo"),
                rs.getString("descricao"),
                rs.getString("embalagem"),
                rs.getBigDecimal("peso")

        );
    }
}
