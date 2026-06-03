package com.logimarui.gateway.api;

import com.logimarui.gateway.api.dto.ManagedServicesOverviewResponseDTO;
import com.logimarui.gateway.api.dto.ManagedServiceStatusResponseDTO;
import com.logimarui.gateway.core.application.ServiceLifecycleManager;
import com.logimarui.gateway.core.application.ServiceStartupReconciler;
import com.logimarui.gateway.core.domain.model.ManagedServiceStatusSnapshot;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.domain.model.StartupReconciliationSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class GatewayServiceAdminController {

    private final ServiceLifecycleManager serviceLifecycleManager;
    private final ServiceStartupReconciler serviceStartupReconciler;

    @PostMapping("/{serviceId}/start")
    public ResponseEntity<ServiceRuntime> start(@PathVariable String serviceId) {
        return ResponseEntity.ok(serviceLifecycleManager.startById(serviceId));
    }

    @PostMapping("/{serviceId}/stop")
    public ResponseEntity<Void> stop(@PathVariable String serviceId) {
        serviceLifecycleManager.stopById(serviceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{serviceId}/restart")
    public ResponseEntity<ServiceRuntime> restart(@PathVariable String serviceId) {
        return ResponseEntity.ok(serviceLifecycleManager.restartById(serviceId));
    }

    @PostMapping("/start-all")
    public ResponseEntity<List<ServiceRuntime>> startAll() {
        return ResponseEntity.ok(serviceLifecycleManager.startAll());
    }

    @PostMapping("/stop-all")
    public ResponseEntity<Void> stopAll() {
        serviceLifecycleManager.stopAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<List<ServiceRuntime>> refresh() {
        return ResponseEntity.ok(serviceLifecycleManager.refreshAll());
    }

    @GetMapping("/runtime")
    public ResponseEntity<List<ServiceRuntime>> findAllRuntimes() {
        return ResponseEntity.ok(serviceLifecycleManager.findAllRuntimes());
    }

    @GetMapping("/startup")
    public ResponseEntity<StartupReconciliationSnapshot> findStartupStatus() {
        return ResponseEntity.ok(serviceStartupReconciler.getSnapshot());
    }

    @GetMapping("/overview")
    public ResponseEntity<ManagedServicesOverviewResponseDTO> findOverview() {
        return ResponseEntity.ok(new ManagedServicesOverviewResponseDTO(
                serviceStartupReconciler.getSnapshot(),
                serviceLifecycleManager.findAllManagedServiceStatuses()
                        .stream()
                        .map(this::toStatusResponse)
                        .toList()
        ));
    }

    private ManagedServiceStatusResponseDTO toStatusResponse(ManagedServiceStatusSnapshot snapshot) {
        return new ManagedServiceStatusResponseDTO(
                snapshot.service().getId(),
                snapshot.service().getPathPrefix(),
                snapshot.service().getType(),
                snapshot.service().getPort(),
                snapshot.service().isRequiresAuthentication(),
                snapshot.service().getPortEnvironmentVariable(),
                snapshot.service().isStartOnBoot(),
                snapshot.running(),
                snapshot.runtime()
        );
    }
}
