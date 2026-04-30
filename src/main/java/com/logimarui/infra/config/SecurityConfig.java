package com.logimarui.infra.config;

import com.logimarui.infra.security.config.JwtAuthenticationFilter;
import com.logimarui.infra.security.jwt.JwtProperties;
import org.springframework.http.HttpMethod;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {
    private static final List<String> FRONTEND_RESERVED_PATH_PREFIXES = List.of(
            "/admin",
            "/api",
            "/auth",
            "/error",
            "/evolution",
            "/form",
            "/form-test",
            "/gerenciador-database",
            "/gerenciador-extracao",
            "/n8n",
            "/replenishments",
            "/rest",
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webhook",
            "/webhook-test"
    );

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/assets/**",
                                "/images/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/auth/refresh",
                                "/auth/forgot-password",
                                "/auth/change-password",
                                "/auth/employees/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/admin/services/**").permitAll()
                        .requestMatchers(
                                "/api/extrator/**",
                                "/api/backup/**",
                                "/api/monitoring/**",
                                "/api/n8n/**",
                                "/api/evolution-api/**",
                                "/rest",
                                "/rest/**",
                                "/webhook",
                                "/webhook/**",
                                "/webhook-test",
                                "/webhook-test/**",
                                "/form",
                                "/form/**",
                                "/form-test",
                                "/form-test/**",
                                "/gerenciador-extracao",
                                "/gerenciador-extracao/**",
                                "/gerenciador-database",
                                "/gerenciador-database/**",
                                "/evolution",
                                "/evolution/**",
                                "/n8n",
                                "/n8n/**"
                        ).permitAll()
                        .requestMatchers(frontendRequestMatcher()).permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    private RequestMatcher frontendRequestMatcher() {
        return request -> {
            String method = request.getMethod();

            if (!HttpMethod.GET.matches(method)
                    && !HttpMethod.HEAD.matches(method)
                    && !HttpMethod.OPTIONS.matches(method)) {
                return false;
            }

            String requestUri = request.getRequestURI();

            return FRONTEND_RESERVED_PATH_PREFIXES.stream()
                    .noneMatch(prefix -> matchesPath(requestUri, prefix));
        };
    }

    private boolean matchesPath(String requestUri, String prefix) {
        return requestUri.equals(prefix)
                || requestUri.startsWith(prefix + "/");
    }
}

