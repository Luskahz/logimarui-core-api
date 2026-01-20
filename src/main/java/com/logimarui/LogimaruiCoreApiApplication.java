package com.logimarui;

import com.logimarui.infra.config.db.ReadDbProperties;
import com.logimarui.infra.config.db.WriteDbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        ReadDbProperties.class,
        WriteDbProperties.class
})
public class LogimaruiCoreApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogimaruiCoreApiApplication.class, args);
    }
}
