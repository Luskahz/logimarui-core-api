package com.logimarui.occurrence.infra.jdbc.mapper;

import com.logimarui.occurrence.core.domain.model.OrderSummary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

@Component
public class OrderSummaryRowMapper implements RowMapper<OrderSummary> {
    @Override
    public OrderSummary mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long invoiceValue = resultSet.getLong("invoice_number");
        Long invoiceNumber = resultSet.wasNull() ? null : invoiceValue;
        Date deliveryDate = resultSet.getDate("delivery_date");
        Date invoiceIssueDate = resultSet.getDate("invoice_issue_date");
        return new OrderSummary(
                resultSet.getLong("order_number"),
                invoiceNumber,
                resultSet.getLong("customer_id"),
                resultSet.getString("customer_name"),
                resultSet.getString("trade_name"),
                deliveryDate == null ? null : deliveryDate.toLocalDate(),
                invoiceIssueDate == null ? null : invoiceIssueDate.toLocalDate(),
                resultSet.getBigDecimal("order_value"),
                resultSet.getBigDecimal("total_hectoliters"),
                resultSet.getBigDecimal("total_weight_kg"),
                nullableLong(resultSet, "route_number"),
                nullableLong(resultSet, "sector_code"),
                resultSet.getString("driver_name"),
                resultSet.getString("order_type"),
                resultSet.getString("external_status")
        );
    }

    private Long nullableLong(ResultSet resultSet, String column) throws SQLException {
        long value = resultSet.getLong(column);
        return resultSet.wasNull() ? null : value;
    }
}
