package com.logimarui.auth.infra.config.security;

import com.logimarui.auth.core.port.AuthTimeProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "security.auth")
public class AuthTimePropertiesImpl implements AuthTimeProperties {
    private Duration refreshTokenTtl;
    private Duration passwordChangeRequestTtl;
    private Duration sessionTtl;

    @Override
    public Duration sessionTtl() {
        return sessionTtl;
    }

    @Override
    public Duration refreshTokenTtl() {
        return refreshTokenTtl;
    }

    @Override
    public Duration passwordChangeRequestTtl() {
        return passwordChangeRequestTtl;
    }
}
