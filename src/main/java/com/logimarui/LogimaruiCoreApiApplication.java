package com.logimarui;

import com.logimarui.infra.config.db.ReadDbProperties;
import com.logimarui.infra.config.db.WriteDbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.logimarui")
@EnableJpaRepositories(basePackages = "com.logimarui")
@EnableConfigurationProperties({
        ReadDbProperties.class,
        WriteDbProperties.class
})
public class LogimaruiCoreApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogimaruiCoreApiApplication.class, args);
    }
}
