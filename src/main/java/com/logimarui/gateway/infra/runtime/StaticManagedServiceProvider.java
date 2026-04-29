package com.logimarui.gateway.infra.runtime;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceType;
import com.logimarui.gateway.core.port.ManagedServiceProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StaticManagedServiceProvider implements ManagedServiceProvider {

    private final List<ManagedService> services = List.of(
            new ManagedService(
                    "extrator-manager",
                    "/api/extrator",
                    ServiceType.PYTHON,
                    "C:\\Users\\lucas.l\\Documents\\LogImarui\\logimarui_services\\extrator-manager",
                    "python app.py",
                    4000,
                    true
            ),
            new ManagedService(
                    "database-monitoring",
                    "/api/monitoring",
                    ServiceType.NODE,
                    "C:\\Users\\lucas.l\\Documents\\LogImarui\\logimarui_services\\database-monitoring",
                    "npm run dev",
                    4001,
                    true
            ),
            new ManagedService(
                    "backup-manager",
                    "/api/backup",
                    ServiceType.NODE,
                    "C:\\Users\\lucas.l\\Documents\\LogImarui\\logimarui_services\\banco-de-dados\\backup-runner",
                    "npm start",
                    4002,
                    true
            ),
            new ManagedService(
                    "n8n",
                    "/api/n8n",
                    ServiceType.DOCKER,
                    "C:\\Users\\lucas.l\\Documents\\LogImarui\\logimarui_services\\n8n",
                    "docker compose up -d",
                    "docker compose down",
                    5678,
                    true,
                    "N8N_LOCAL_PORT"
            ),
            new ManagedService(
                    "evolution-api",
                    "/api/evolution-api",
                    ServiceType.DOCKER,
                    "C:\\Users\\lucas.l\\Documents\\LogImarui\\logimarui_services\\evolution",
                    "docker compose --env-file .env -p evolution -f docker-compose.yaml up -d",
                    "docker compose --env-file .env -p evolution -f docker-compose.yaml down",
                    4080,
                    true,
                    "EVOLUTION_API_PORT"
            ),
            new ManagedService(
                    "frontend",
                    "/",
                    ServiceType.NODE,
                    "C:\\Users\\lucas.l\\Documents\\LogImarui\\logImarui-frontend",
                    "npm start",
                    3000,
                    false,
                    true
            )
    );

    @Override
    public List<ManagedService> findAll() {
        return services;
    }

    @Override
    public Optional<ManagedService> findById(String id) {
        return services.stream()
                .filter(service -> service.getId().equals(id))
                .findFirst();
    }
}
