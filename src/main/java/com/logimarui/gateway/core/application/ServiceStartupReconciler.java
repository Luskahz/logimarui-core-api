package com.logimarui.gateway.core.application;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.domain.model.StartupReconciliationSnapshot;
import com.logimarui.gateway.core.domain.model.StartupReconciliationStatus;
import com.logimarui.gateway.core.port.ManagedServiceProvider;
import com.logimarui.gateway.core.port.ServiceRuntimeRepository;
import com.logimarui.gateway.infra.runtime.ManagedServiceIds;
import com.logimarui.gateway.infra.process.ProcessTreeTerminator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceStartupReconciler {

    private final ManagedServiceProvider managedServiceProvider;
    private final ServiceRuntimeRepository serviceRuntimeRepository;
    private final ServiceLifecycleManager serviceLifecycleManager;
    private final ProcessTreeTerminator processTreeTerminator;

    private volatile StartupReconciliationStatus status = StartupReconciliationStatus.NOT_STARTED;
    private volatile String currentServiceId;
    private volatile String errorMessage;
    private volatile Instant startedAt;
    private volatile Instant finishedAt;

    public StartupReconciliationSnapshot getSnapshot() {
        return new StartupReconciliationSnapshot(
                status,
                currentServiceId,
                errorMessage,
                startedAt,
                finishedAt
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    public void reconcileOnStartup() {
        status = StartupReconciliationStatus.RUNNING;
        currentServiceId = null;
        errorMessage = null;
        startedAt = Instant.now();
        finishedAt = null;

        log.info("[Supervisor] Reconcile iniciado.");

        try {
            for (ManagedService service : managedServiceProvider.findAll()) {
                currentServiceId = service.getId();

                if (!service.isStartOnBoot()) {
                    log.info(
                            "[Supervisor] Servico {} configurado com startOnBoot=false. Boot automatico ignorado.",
                            service.getId()
                    );
                    continue;
                }

                log.info(
                        "[Supervisor] Reconciliando servico: {} porta preferida {}",
                        service.getId(),
                        service.getPort()
                );

                stopPersistedRuntimeIfExists(service);
                startFresh(service);
            }

            currentServiceId = null;
            status = StartupReconciliationStatus.COMPLETED;
            finishedAt = Instant.now();

            log.info("[Supervisor] Reconcile finalizado.");
        } catch (RuntimeException exception) {
            status = StartupReconciliationStatus.FAILED;
            errorMessage = exception.getMessage();
            finishedAt = Instant.now();
            throw exception;
        } catch (Error error) {
            status = StartupReconciliationStatus.FAILED;
            errorMessage = error.getMessage();
            finishedAt = Instant.now();
            throw error;
        }
    }

    private void stopPersistedRuntimeIfExists(ManagedService service) {
        Optional<ServiceRuntime> runtime = findPersistedRuntime(service);

        if (runtime.isEmpty()) {
            log.info("[Supervisor] Nenhum runtime persistido para {}", service.getId());
            return;
        }

        ServiceRuntime persistedRuntime = runtime.get();

        log.info(
                "[Supervisor] Matando runtime persistido {} rootPid={} listenerPid={}",
                service.getId(),
                persistedRuntime.getRootPid(),
                persistedRuntime.getListenerPid()
        );

        processTreeTerminator.terminate(persistedRuntime.getListenerPid());
        processTreeTerminator.terminate(persistedRuntime.getRootPid());

        deletePersistedRuntime(service);
    }

    private void startFresh(ManagedService service) {
        log.info("[Supervisor] Subindo servico {}", service.getId());

        ServiceRuntime runtime = serviceLifecycleManager.startById(service.getId());

        log.info(
                "[Supervisor] Servico {} iniciado. rootPid={} listenerPid={} port={}",
                runtime.getServiceId(),
                runtime.getRootPid(),
                runtime.getListenerPid(),
                runtime.getPort()
        );
    }

    private Optional<ServiceRuntime> findPersistedRuntime(ManagedService service) {
        for (String serviceId : resolveServiceAliases(service)) {
            Optional<ServiceRuntime> runtime = serviceRuntimeRepository.findByServiceId(serviceId);

            if (runtime.isPresent()) {
                return runtime;
            }
        }

        return Optional.empty();
    }

    private void deletePersistedRuntime(ManagedService service) {
        for (String serviceId : resolveServiceAliases(service)) {
            serviceRuntimeRepository.deleteByServiceId(serviceId);
        }
    }

    private List<String> resolveServiceAliases(ManagedService service) {
        return ManagedServiceIds.aliasesFor(service.getId());
    }
}
