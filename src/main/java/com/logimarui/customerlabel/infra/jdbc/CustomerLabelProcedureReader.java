package com.logimarui.customerlabel.infra.jdbc;

import com.logimarui.customerlabel.infra.jpa.entity.CustomerLabelCacheEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomerLabelProcedureReader {
    private static final String PROCEDURE_CALL = "CALL sp_cluster_clientes(?, ?, ?, ?)";
    private static final String PROCEDURE_ORIGIN = "promax";

    private final JdbcTemplate jdbcTemplate;

    public CustomerLabelProcedureReader(
            @Qualifier("clusterProcedureJdbcTemplate") JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CustomerLabelCacheEntity> readSnapshot(LocalDate referenceDate, Instant generatedAt) {
        Map<Long, CustomerLabelCacheEntity> labelsByCustomer = new LinkedHashMap<>();

        jdbcTemplate.query(
                connection -> {
                    var statement = connection.prepareStatement(PROCEDURE_CALL);
                    statement.setNull(1, Types.BIGINT);
                    statement.setDate(2, Date.valueOf(referenceDate));
                    statement.setDate(3, Date.valueOf(referenceDate));
                    statement.setString(4, PROCEDURE_ORIGIN);
                    return statement;
                },
                resultSet -> {
                    Long customerId = resultSet.getObject("cod_cliente", Long.class);
                    Object rawLabel = resultSet.getObject("cluster");
                    if (customerId != null && rawLabel != null) {
                        labelsByCustomer.put(
                                customerId,
                                new CustomerLabelCacheEntity(
                                        customerId,
                                        String.valueOf(rawLabel),
                                        generatedAt
                                )
                        );
                    }
                }
        );

        return List.copyOf(labelsByCustomer.values());
    }
}
