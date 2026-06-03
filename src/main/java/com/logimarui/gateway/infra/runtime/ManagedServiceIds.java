package com.logimarui.gateway.infra.runtime;

import java.util.List;
import java.util.Map;

public final class ManagedServiceIds {

    private static final Map<String, String> LEGACY_TO_CANONICAL = Map.of(
            "extrator-manager", "gerenciador-extracao",
            "database-monitoring", "gerenciador-database-monitoring",
            "backup-manager", "gerenciador-database-backup",
            "n8n", "n8n-interno",
            "evolution-api", "evolution-interno"
    );

    private static final Map<String, List<String>> CANONICAL_TO_ALIASES = Map.of(
            "gerenciador-extracao", List.of("gerenciador-extracao", "extrator-manager"),
            "gerenciador-database-monitoring", List.of("gerenciador-database-monitoring", "database-monitoring"),
            "gerenciador-database-backup", List.of("gerenciador-database-backup", "backup-manager"),
            "n8n-interno", List.of("n8n-interno", "n8n"),
            "evolution-interno", List.of("evolution-interno", "evolution-api"),
            "frontend", List.of("frontend")
    );

    private ManagedServiceIds() {
    }

    public static String toCanonical(String serviceId) {
        if (serviceId == null || serviceId.isBlank()) {
            return serviceId;
        }

        return LEGACY_TO_CANONICAL.getOrDefault(serviceId, serviceId);
    }

    public static List<String> aliasesFor(String serviceId) {
        String canonicalId = toCanonical(serviceId);

        return CANONICAL_TO_ALIASES.getOrDefault(canonicalId, List.of(canonicalId));
    }
}
