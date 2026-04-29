package com.logimarui.infra.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    private final String[] allowedOrigins;

    public CorsConfig(
            @Value("${app.cors.allowed-origins:http://localhost,http://127.0.0.1,http://localhost:3000,http://127.0.0.1:3000,http://localhost:8081,http://127.0.0.1:8081,http://localhost:19006,http://127.0.0.1:19006}") String allowedOrigins
    ) {
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toArray(String[]::new);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                        .allowedOriginPatterns(allowedOrigins)
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "PATCH",
                                "OPTIONS"
                        )
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
