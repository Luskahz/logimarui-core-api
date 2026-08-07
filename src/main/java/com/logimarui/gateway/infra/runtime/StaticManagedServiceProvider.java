package com.logimarui.gateway.infra.runtime;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceType;
import com.logimarui.gateway.core.port.ManagedServiceProvider;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Component
public class StaticManagedServiceProvider implements ManagedServiceProvider {

    private final List<ManagedService> services;

    public StaticManagedServiceProvider(Environment environment) {
        boolean developmentProfile = isDevelopmentProfile(environment);
        Path workspaceRoot = resolveWorkspaceRoot(environment.getProperty("LOGIMARUI_ROOT", ""));
        Path servicesRoot = resolveServicesRoot(
                environment.getProperty("LOGIMARUI_SERVICES_ROOT", ""),
                workspaceRoot
        );
        Path frontendRoot = workspaceRoot.resolve("logImarui-frontend").normalize();

        this.services = List.of(
                buildService(
                        environment,
                        "SERVICE_EXTRATOR",
                        "gerenciador-extracao",
                        "/api/extrator",
                        ServiceType.PYTHON,
                        servicesRoot.resolve("extrator-manager"),
                        "python app.py",
                        null,
                        developmentProfile ? 4100 : 4000,
                        true,
                        "PORT",
                        true,
                        true
                ),
                buildService(
                        environment,
                        "SERVICE_MONITORING",
                        "gerenciador-database-monitoring",
                        "/api/monitoring",
                        ServiceType.NODE,
                        servicesRoot.resolve("database-monitoring"),
                        "npm start",
                        null,
                        developmentProfile ? 4101 : 4001,
                        true,
                        "PORT",
                        true,
                        true
                ),
                buildService(
                        environment,
                        "SERVICE_BACKUP",
                        "gerenciador-database-backup",
                        "/api/backup",
                        ServiceType.NODE,
                        servicesRoot.resolve("banco-de-dados").resolve("backup-runner"),
                        "npm start",
                        null,
                        developmentProfile ? 4102 : 4002,
                        true,
                        "PORT",
                        true,
                        true
                ),
                buildService(
                        environment,
                        "SERVICE_SAVI",
                        "automacao-savi",
                        "/api/savi",
                        ServiceType.PYTHON,
                        servicesRoot.resolve("automacao-savi"),
                        "python -m savi_automation api --host 127.0.0.1",
                        null,
                        developmentProfile ? 4103 : 4003,
                        true,
                        "PORT",
                        true,
                        true
                ),
                buildService(
                        environment,
                        "SERVICE_N8N",
                        "n8n-interno",
                        "/api/n8n",
                        ServiceType.DOCKER,
                        servicesRoot.resolve("n8n"),
                        "docker compose up -d",
                        "docker compose down",
                        developmentProfile ? 5679 : 5678,
                        true,
                        "N8N_LOCAL_PORT",
                        false,
                        false
                ),
                buildService(
                        environment,
                        "SERVICE_EVOLUTION",
                        "evolution-interno",
                        "/api/evolution-api",
                        ServiceType.DOCKER,
                        servicesRoot.resolve("evolution"),
                        "docker compose --env-file .env -p evolution -f docker-compose.yaml up -d",
                        "docker compose --env-file .env -p evolution -f docker-compose.yaml down",
                        developmentProfile ? 4081 : 4080,
                        true,
                        "EVOLUTION_API_PORT",
                        false,
                        false
                ),
                buildService(
                        environment,
                        "SERVICE_FRONTEND",
                        "frontend",
                        "/",
                        ServiceType.NODE,
                        frontendRoot,
                        "npm run build-start",
                        null,
                        developmentProfile ? 8191 : 8091,
                        true,
                        "FRONTEND_PORT",
                        true,
                        true
                )
        );
    }

    private boolean isDevelopmentProfile(Environment environment) {
        String profile = normalizeString(environment.getProperty("LOGIMARUI_PROFILE"));

        if (!profile.isBlank()) {
            return "dev".equalsIgnoreCase(profile) || "development".equalsIgnoreCase(profile);
        }

        return parseBoolean(environment.getProperty("LOGIMARUI_DEV_MODE"), false);
    }

    @Override
    public List<ManagedService> findAll() {
        return services;
    }

    @Override
    public Optional<ManagedService> findById(String id) {
        String canonicalId = ManagedServiceIds.toCanonical(id);

        return services.stream()
                .filter(service -> service.getId().equals(canonicalId))
                .findFirst();
    }

    private ManagedService buildService(
            Environment environment,
            String envPrefix,
            String defaultId,
            String defaultPathPrefix,
            ServiceType defaultType,
            Path defaultWorkingDirectory,
            String defaultStartCommand,
            String defaultStopCommand,
            int defaultPort,
            boolean defaultRequiresAuthentication,
            String defaultPortEnvironmentVariable,
            boolean defaultStartOnBoot,
            boolean defaultEnabled
    ) {
        String rawServiceId = normalizeString(environment.getProperty(envPrefix + "_ID"));
        String resolvedServiceId = rawServiceId.isBlank()
                ? defaultId
                : ManagedServiceIds.toCanonical(rawServiceId);

        return new ManagedService(
                resolvedServiceId,
                environment.getProperty(envPrefix + "_PATH_PREFIX", defaultPathPrefix),
                parseServiceType(environment.getProperty(envPrefix + "_TYPE"), defaultType),
                environment.getProperty(
                        envPrefix + "_WORKDIR",
                        defaultWorkingDirectory.toString()
                ),
                environment.getProperty(envPrefix + "_COMMAND", defaultStartCommand),
                normalizeNullableString(environment.getProperty(envPrefix + "_STOP_COMMAND"), defaultStopCommand),
                parseInteger(environment.getProperty(envPrefix + "_PREFERRED_PORT"), defaultPort),
                parseBoolean(
                        environment.getProperty(envPrefix + "_REQUIRES_AUTHENTICATION"),
                        defaultRequiresAuthentication
                ),
                environment.getProperty(
                        envPrefix + "_PORT_ENV_VAR",
                        defaultPortEnvironmentVariable
                ),
                parseBoolean(
                        environment.getProperty(envPrefix + "_START_ON_BOOT"),
                        defaultStartOnBoot
                ),
                parseBoolean(
                        environment.getProperty(envPrefix + "_ENABLED"),
                        defaultEnabled
                )
        );
    }

    private Path resolveWorkspaceRoot(String configuredWorkspaceRoot) {
        if (configuredWorkspaceRoot != null && !configuredWorkspaceRoot.isBlank()) {
            return Path.of(configuredWorkspaceRoot).normalize();
        }

        Path currentDirectory = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        Path currentDirectoryName = currentDirectory.getFileName();

        if (currentDirectoryName != null && "logimarui-core-api".equalsIgnoreCase(currentDirectoryName.toString())) {
            Path parent = currentDirectory.getParent();

            if (parent != null) {
                return parent.normalize();
            }
        }

        return currentDirectory;
    }

    private Path resolveServicesRoot(String configuredServicesRoot, Path workspaceRoot) {
        if (configuredServicesRoot != null && !configuredServicesRoot.isBlank()) {
            return Path.of(configuredServicesRoot).normalize();
        }

        return workspaceRoot.resolve("logimarui_services").normalize();
    }

    private ServiceType parseServiceType(String rawValue, ServiceType fallback) {
        String normalizedValue = normalizeString(rawValue);

        if (normalizedValue.isBlank()) {
            return fallback;
        }

        try {
            return ServiceType.valueOf(normalizedValue.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }

    private int parseInteger(String rawValue, int fallback) {
        String normalizedValue = normalizeString(rawValue);

        if (normalizedValue.isBlank()) {
            return fallback;
        }

        try {
            return Integer.parseInt(normalizedValue);
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private boolean parseBoolean(String rawValue, boolean fallback) {
        String normalizedValue = normalizeString(rawValue);

        if (normalizedValue.isBlank()) {
            return fallback;
        }

        return Boolean.parseBoolean(normalizedValue);
    }

    private String normalizeString(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeNullableString(String value, String fallback) {
        String normalizedValue = normalizeString(value);
        return normalizedValue.isBlank() ? fallback : normalizedValue;
    }
}
