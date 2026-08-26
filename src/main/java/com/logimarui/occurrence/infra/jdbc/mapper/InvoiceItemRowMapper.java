package com.logimarui.occurrence.infra.jdbc.mapper;

import com.logimarui.occurrence.core.domain.model.InvoiceItem;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class InvoiceItemRowMapper implements RowMapper<InvoiceItem> {
    @Override
    public InvoiceItem mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new InvoiceItem(
                resultSet.getLong("product_code"),
                resultSet.getString("product_name"),
                resultSet.getBigDecimal("quantity")
        );
    }
}
