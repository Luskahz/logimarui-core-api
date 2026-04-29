package com.logimarui.gateway.api;

import com.logimarui.gateway.core.application.ServiceLifecycleManager;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class GatewayServiceAdminController {

    private final ServiceLifecycleManager serviceLifecycleManager;

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
}