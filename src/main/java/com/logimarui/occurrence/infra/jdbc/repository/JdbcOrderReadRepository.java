package com.logimarui.occurrence.infra.jdbc.repository;

import com.logimarui.occurrence.core.domain.model.InvoiceItem;
import com.logimarui.occurrence.core.domain.model.OrderSummary;
import com.logimarui.occurrence.core.domain.model.ReturnAlertContext;
import com.logimarui.occurrence.core.port.repository.OrderReadRepository;
import com.logimarui.occurrence.infra.jdbc.mapper.InvoiceItemRowMapper;
import com.logimarui.occurrence.infra.jdbc.mapper.OrderSummaryRowMapper;
import com.logimarui.occurrence.infra.jdbc.mapper.ReturnAlertContextRowMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JdbcOrderReadRepository implements OrderReadRepository {
    private static final String SOURCE_TABLE = "cora_consulta_de_pedidos";

    private static final Map<String, String> ORDER_SORT_COLUMNS = Map.of(
            "orderNumber", "order_number",
            "invoiceNumber", "invoice_number",
            "deliveryDate", "delivery_date",
            "invoiceIssueDate", "invoice_issue_date",
            "orderValue", "order_value",
            "totalHectoliters", "total_hectoliters"
    );

    private static final Map<String, String> ITEM_SORT_COLUMNS = Map.of(
            "productCode", "product_code",
            "productName", "product_name",
            "quantity", "quantity"
    );

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OrderSummaryRowMapper orderSummaryRowMapper;
    private final InvoiceItemRowMapper invoiceItemRowMapper;
    private final ReturnAlertContextRowMapper returnAlertContextRowMapper;

    public JdbcOrderReadRepository(
            @Qualifier("readNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate,
            OrderSummaryRowMapper orderSummaryRowMapper,
            InvoiceItemRowMapper invoiceItemRowMapper,
            ReturnAlertContextRowMapper returnAlertContextRowMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderSummaryRowMapper = orderSummaryRowMapper;
        this.invoiceItemRowMapper = invoiceItemRowMapper;
        this.returnAlertContextRowMapper = returnAlertContextRowMapper;
    }

    @Override
    public Page<OrderSummary> findCustomerOrders(
            Long customerId,
            LocalDate date,
            Pageable pageable
    ) {
        String dateClause = date == null
                ? ""
                : " AND data_entrega >= :dateStart AND data_entrega < :dateEnd";

        String contentSql = """
                SELECT
                    numero_pedido AS order_number,
                    numero_nf AS invoice_number,
                    DATE(data_entrega) AS delivery_date,
                    DATE(data_emissao_nf) AS invoice_issue_date,
                    COALESCE(MAX(valor_total_nf), MAX(valor_total)) AS order_value,
                    COALESCE(SUM(volume_hectolitro), 0) AS total_hectoliters
                FROM %s
                WHERE cod_cliente = :customerId%s
                GROUP BY
                    numero_pedido,
                    numero_nf,
                    DATE(data_entrega),
                    DATE(data_emissao_nf)
                ORDER BY %s
                LIMIT :limit OFFSET :offset
                """.formatted(
                SOURCE_TABLE,
                dateClause,
                orderBy(pageable, ORDER_SORT_COLUMNS, "delivery_date DESC, order_number DESC")
        );

        String countSql = """
                SELECT COUNT(*)
                FROM (
                    SELECT 1
                    FROM %s
                    WHERE cod_cliente = :customerId%s
                    GROUP BY
                        numero_pedido,
                        numero_nf,
                        DATE(data_entrega),
                        DATE(data_emissao_nf)
                ) grouped_orders
                """.formatted(SOURCE_TABLE, dateClause);

        MapSqlParameterSource parameters = pageParameters(pageable)
                .addValue("customerId", customerId);
        if (date != null) {
            parameters.addValue("dateStart", date.atStartOfDay());
            parameters.addValue("dateEnd", date.plusDays(1).atStartOfDay());
        }

        var content = jdbcTemplate.query(contentSql, parameters, orderSummaryRowMapper);
        Long total = jdbcTemplate.queryForObject(countSql, parameters, Long.class);
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<InvoiceItem> findInvoiceItems(Long invoiceNumber, Pageable pageable) {
        String contentSql = """
                SELECT
                    cod_produto AS product_code,
                    MAX(desc_produto) AS product_name,
                    SUM(quant_venda) AS quantity
                FROM %s
                WHERE numero_nf = :invoiceNumber
                GROUP BY cod_produto
                ORDER BY %s
                LIMIT :limit OFFSET :offset
                """.formatted(
                SOURCE_TABLE,
                orderBy(pageable, ITEM_SORT_COLUMNS, "product_code ASC")
        );

        String countSql = """
                SELECT COUNT(DISTINCT cod_produto)
                FROM %s
                WHERE numero_nf = :invoiceNumber
                """.formatted(SOURCE_TABLE);

        MapSqlParameterSource parameters = pageParameters(pageable)
                .addValue("invoiceNumber", invoiceNumber);
        var content = jdbcTemplate.query(contentSql, parameters, invoiceItemRowMapper);
        Long total = jdbcTemplate.queryForObject(countSql, parameters, Long.class);
        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Optional<ReturnAlertContext> findReturnContext(Long customerId, Long invoiceNumber) {
        String sql = """
                SELECT
                    cod_cliente AS customer_id,
                    numero_nf AS invoice_number,
                    COALESCE(MAX(valor_total_nf), MAX(valor_total)) AS order_value,
                    COALESCE(SUM(volume_hectolitro), 0) AS total_hectoliters
                FROM %s
                WHERE cod_cliente = :customerId
                  AND numero_nf = :invoiceNumber
                GROUP BY cod_cliente, numero_nf
                """.formatted(SOURCE_TABLE);

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("customerId", customerId)
                .addValue("invoiceNumber", invoiceNumber);
        return jdbcTemplate.query(sql, parameters, returnAlertContextRowMapper)
                .stream()
                .findFirst();
    }

    private MapSqlParameterSource pageParameters(Pageable pageable) {
        return new MapSqlParameterSource()
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());
    }

    private String orderBy(
            Pageable pageable,
            Map<String, String> allowedColumns,
            String defaultOrder
    ) {
        if (pageable.getSort().isUnsorted()) {
            return defaultOrder;
        }

        return pageable.getSort().stream()
                .map(order -> {
                    String column = allowedColumns.get(order.getProperty());
                    if (column == null) {
                        throw new IllegalArgumentException(
                                "Unsupported READ sort field: " + order.getProperty()
                        );
                    }
                    return column + " " + order.getDirection().name();
                })
                .collect(Collectors.joining(", "));
    }
}
