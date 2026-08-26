package com.logimarui.platform.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(ReadDbProperties.class)
public class ReadDbConfig {

    @Bean(name = "readDataSource")
    public DataSource readDataSource(ReadDbProperties props) {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(props.getJdbcUrl())
                .username(props.getUsername())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .build();

        dataSource.setPoolName("logimarui-read-pool");
        dataSource.setMaximumPoolSize(props.getMaximumPoolSize());
        dataSource.setReadOnly(true);
        dataSource.setConnectionInitSql("SET SESSION TRANSACTION READ ONLY");
        return dataSource;
    }

    @Bean(name = "readJdbcTemplate")
    public JdbcTemplate readJdbcTemplate(@Qualifier("readDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "readNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate readNamedParameterJdbcTemplate(
            @Qualifier("readDataSource") DataSource dataSource
    ) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "readTransactionManager")
    public PlatformTransactionManager readTransactionManager(
            @Qualifier("readDataSource") DataSource dataSource
    ) {
        return new DataSourceTransactionManager(dataSource);
    }
}
