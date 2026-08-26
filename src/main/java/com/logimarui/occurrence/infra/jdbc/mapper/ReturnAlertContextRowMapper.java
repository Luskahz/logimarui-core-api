package com.logimarui.occurrence.infra.jdbc.mapper;

import com.logimarui.occurrence.core.domain.model.ReturnAlertContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReturnAlertContextRowMapper implements RowMapper<ReturnAlertContext> {
    @Override
    public ReturnAlertContext mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new ReturnAlertContext(
                resultSet.getLong("customer_id"),
                resultSet.getLong("invoice_number"),
                resultSet.getBigDecimal("order_value"),
                resultSet.getBigDecimal("total_hectoliters")
        );
    }
}
