package com.logimarui.gateway.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceRouteTest {

    @Test
    void rejectsBackendApiRootAndCoreApiNamespace() {
        for (String pathPrefix : List.of(
                "/api",
                "/api/",
                "/api/v1",
                "/api/v1/",
                "/api/v1/authentication"
        )) {
            assertThatThrownBy(() -> serviceRoute(pathPrefix))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(pathPrefix);
        }
    }

    @Test
    void acceptsSpecificExternalApiPrefixesAndFrontendRoot() {
        for (String pathPrefix : List.of(
                "/api/extrator",
                "/api/monitoring",
                "/api/backup",
                "/api/savi",
                "/api/n8n",
                "/api/evolution-api",
                "/"
        )) {
            assertThat(serviceRoute(pathPrefix).getPathPrefix())
                    .isEqualTo(pathPrefix);
        }
    }

    private ServiceRoute serviceRoute(String pathPrefix) {
        return new ServiceRoute(
                "test-service",
                pathPrefix,
                "http://127.0.0.1:9000",
                true
        );
    }
}
