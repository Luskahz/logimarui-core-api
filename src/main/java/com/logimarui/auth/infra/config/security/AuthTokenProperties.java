package com.logimarui.auth.infra.config.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.auth")
public class AuthTokenProperties {
    @Getter  private Duration refreshTokenTtl;
}
