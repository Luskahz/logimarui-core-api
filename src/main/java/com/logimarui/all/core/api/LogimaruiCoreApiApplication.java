package com.logimarui.all.core.api;

import com.logimarui.all.core.api.config.db.ReadDbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ReadDbProperties.class)
public class LogimaruiCoreApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogimaruiCoreApiApplication.class, args);
    }
}
