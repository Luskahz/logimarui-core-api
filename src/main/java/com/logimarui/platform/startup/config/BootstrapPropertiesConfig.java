package com.logimarui.platform.startup.config;

import com.logimarui.platform.startup.properties.AdminBootstrapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AdminBootstrapProperties.class)
public class BootstrapPropertiesConfig {
}
