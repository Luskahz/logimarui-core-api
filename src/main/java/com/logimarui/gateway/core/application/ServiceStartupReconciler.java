package com.logimarui.gateway.core.application;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.port.ManagedServiceProvider;
import com.logimarui.gateway.core.port.ServiceRuntimeRepository;
import com.logimarui.gateway.infra.process.ProcessTreeTerminator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceStartupReconciler {

    private final ManagedServiceProvider managedServiceProvider;
    private final ServiceRuntimeRepository serviceRuntimeRepository;
    private final ServiceLifecycleManager serviceLifecycleManager;
    private final ProcessTreeTerminator processTreeTerminator;

    @EventListener(ApplicationReadyEvent.class)
    public void reconcileOnStartup() {
        log.info("[Supervisor] Reconcile iniciado.");

        for (ManagedService service : managedServiceProvider.findAll()) {
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

        log.info("[Supervisor] Reconcile finalizado.");
    }

    private void stopPersistedRuntimeIfExists(ManagedService service) {
        Optional<ServiceRuntime> runtime =
                serviceRuntimeRepository.findByServiceId(service.getId());

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

        serviceRuntimeRepository.deleteByServiceId(service.getId());
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
}
