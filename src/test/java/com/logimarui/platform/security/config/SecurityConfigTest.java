package com.logimarui.platform.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(null);

    @Test
    void uiRoutesArePublicOnlyForFrontendMethods() {
        List<String> uiPaths = List.of(
                "/admin",
                "/authorization",
                "/authentication",
                "/replenishments",
                "/app/extrator",
                "/rota-next-criada-amanha"
        );

        for (String path : uiPaths) {
            assertThat(isFrontendRequest(HttpMethod.GET, path)).isTrue();
            assertThat(isFrontendRequest(HttpMethod.HEAD, path)).isTrue();
            assertThat(isFrontendRequest(HttpMethod.OPTIONS, path)).isTrue();
            assertThat(isFrontendRequest(HttpMethod.POST, path)).isFalse();
        }
    }

    @Test
    void backendInfrastructureAndExternalContractsAreNeverClassifiedAsFrontend() {
        List<String> nonFrontendPaths = List.of(
                "/api/v1/authentication/me",
                "/api/extrator/health",
                "/actuator/health",
                "/docs",
                "/error",
                "/n8n/",
                "/evolution/",
                "/gerenciador-database/monitoring/"
        );

        for (String path : nonFrontendPaths) {
            assertThat(isFrontendRequest(HttpMethod.GET, path)).isFalse();
        }
    }

    private boolean isFrontendRequest(HttpMethod method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method.name(), path);
        request.setRequestURI(path);
        return securityConfig.isFrontendRequest(request);
    }
}
