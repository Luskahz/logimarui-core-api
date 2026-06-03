package com.logimarui.platform.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.datasource.read")
public class ReadDbProperties {

    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;


}
