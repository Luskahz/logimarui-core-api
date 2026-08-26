package com.logimarui.occurrence.infra.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import com.logimarui.occurrence.infra.jdbc.mapper.InvoiceItemRowMapper;
import com.logimarui.occurrence.infra.jdbc.mapper.OrderSummaryRowMapper;
import com.logimarui.occurrence.infra.jdbc.mapper.ReturnAlertContextRowMapper;
import com.logimarui.occurrence.infra.jdbc.repository.JdbcOrderReadRepository;
import com.logimarui.platform.db.ReadDbConfig;
import com.logimarui.platform.db.ReadDbProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class JdbcOrderReadRepositoryIntegrationTest {
    private HikariDataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private JdbcOrderReadRepository repository;

    @BeforeEach
    void setUp() {
        String password = System.getenv("READ_DB_PASSWORD");
        assumeTrue(password != null && !password.isBlank(), "READ_DB_PASSWORD not configured");

        ReadDbProperties properties = new ReadDbProperties();
        properties.setJdbcUrl(System.getenv().getOrDefault(
                "READ_DB_URL",
                "jdbc:mysql://localhost:3306/diretorio?useSSL=false&allowPublicKeyRetrieval=true"
                        + "&serverTimezone=America/Sao_Paulo&useServerPrepStmts=true"
                        + "&readOnlyPropagatesToServer=true"
        ));
        properties.setUsername(System.getenv().getOrDefault("READ_DB_USERNAME", "logimarui-core-api"));
        properties.setPassword(password);
        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        properties.setMaximumPoolSize(2);

        dataSource = (HikariDataSource) new ReadDbConfig().readDataSource(properties);
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        repository = new JdbcOrderReadRepository(
                jdbcTemplate,
                new OrderSummaryRowMapper(),
                new InvoiceItemRowMapper(),
                new ReturnAlertContextRowMapper()
        );
    }

    @AfterEach
    void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void connectionAndServerSessionAreReadOnly() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.isReadOnly()).isTrue();
            try (var statement = connection.createStatement();
                 var resultSet = statement.executeQuery("SELECT @@session.transaction_read_only")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
            }
        }
    }

    @Test
    void executesAllReadQueriesAgainstRealSchema() {
        Map<String, Object> sample = jdbcTemplate.queryForMap("""
                SELECT
                    cod_cliente AS customer_id,
                    numero_nf AS invoice_number,
                    DATE(data_entrega) AS delivery_date
                FROM cora_consulta_de_pedidos
                WHERE numero_nf IS NOT NULL
                ORDER BY data_entrega DESC
                LIMIT 1
                """, Map.of());

        Long customerId = ((Number) sample.get("customer_id")).longValue();
        Long invoiceNumber = ((Number) sample.get("invoice_number")).longValue();
        LocalDate deliveryDate = ((java.sql.Date) sample.get("delivery_date")).toLocalDate();

        var orders = repository.findCustomerOrders(
                customerId,
                deliveryDate,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "deliveryDate"))
        );
        var items = repository.findInvoiceItems(
                invoiceNumber,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "productCode"))
        );
        var context = repository.findReturnContext(customerId, invoiceNumber);

        assertThat(orders.getContent()).isNotEmpty();
        assertThat(items.getContent()).isNotEmpty();
        assertThat(context).isPresent();
        assertThat(context.orElseThrow().customerId()).isEqualTo(customerId);
        assertThat(context.orElseThrow().invoiceNumber()).isEqualTo(invoiceNumber);
    }
}
