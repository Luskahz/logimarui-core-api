package com.logimarui.gateway.core.application;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.port.ManagedServiceProvider;
import com.logimarui.gateway.core.port.ServiceProcessRunner;
import com.logimarui.gateway.core.port.ServiceRuntimeRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceLifecycleManager {

    private final ManagedServiceProvider managedServiceProvider;
    private final ServiceRuntimeRepository serviceRuntimeRepository;
    private final List<ServiceProcessRunner> processRunners;

    public ServiceRuntime startById(String serviceId) {
        ManagedService service = findManagedService(serviceId);

        serviceRuntimeRepository.findByServiceId(serviceId)
                .ifPresent(existingRuntime -> {
                    throw new IllegalStateException(
                            "Serviço já possui runtime registrado: " + serviceId +
                                    " rootPid=" + existingRuntime.getRootPid() +
                                    " listenerPid=" + existingRuntime.getListenerPid()
                    );
                });

        ServiceProcessRunner runner = findRunner(service);

        ServiceRuntime runtime = runner.start(service);
        serviceRuntimeRepository.save(runtime);

        return runtime;
    }

    public void stopById(String serviceId) {
        ManagedService service = findManagedService(serviceId);

        ServiceRuntime runtime = serviceRuntimeRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new IllegalStateException(
                        "Serviço não possui runtime registrado: " + serviceId
                ));

        ServiceProcessRunner runner = findRunner(service);

        try {
            runner.stop(service, runtime);
        } finally {
            serviceRuntimeRepository.deleteByServiceId(serviceId);
        }
    }

    public ServiceRuntime restartById(String serviceId) {
        serviceRuntimeRepository.findByServiceId(serviceId)
                .ifPresent(runtime -> stopById(serviceId));

        return startById(serviceId);
    }

    public List<ServiceRuntime> startAll() {
        return managedServiceProvider.findAll()
                .stream()
                .map(service -> startById(service.getId()))
                .toList();
    }

    public void stopAll() {
        List<ServiceRuntime> runtimes = serviceRuntimeRepository.findAll();

        log.info("[Supervisor] stopAll iniciado. {} runtime(s) encontrado(s).", runtimes.size());

        for (ServiceRuntime runtime : runtimes) {
            try {
                log.info(
                        "[Supervisor] Parando serviço {} rootPid={} listenerPid={} port={}",
                        runtime.getServiceId(),
                        runtime.getRootPid(),
                        runtime.getListenerPid(),
                        runtime.getPort()
                );

                stopById(runtime.getServiceId());

                log.info("[Supervisor] Serviço {} parado.", runtime.getServiceId());
            } catch (Exception exception) {
                log.error("[Supervisor] Erro ao parar serviço {}.", runtime.getServiceId(), exception);
                serviceRuntimeRepository.deleteByServiceId(runtime.getServiceId());
            }
        }
    }

    public List<ServiceRuntime> refreshAll() {
        stopAll();
        return startAll();
    }

    public List<ServiceRuntime> findAllRuntimes() {
        return serviceRuntimeRepository.findAll();
    }

    @PreDestroy
    public void shutdown() {
        log.info("[Supervisor] Shutdown iniciado. Parando serviços gerenciados.");

        try {
            stopAll();
            log.info("[Supervisor] Shutdown finalizado. Serviços gerenciados foram parados.");
        } catch (Exception exception) {
            log.error("[Supervisor] Erro durante shutdown dos serviços gerenciados.", exception);
        }
    }

    private ManagedService findManagedService(String serviceId) {
        return managedServiceProvider.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Serviço não encontrado: " + serviceId
                ));
    }

    private ServiceProcessRunner findRunner(ManagedService service) {
        return processRunners.stream()
                .filter(candidate -> candidate.supports(service))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nenhum runner encontrado para o serviço: " + service.getId()
                ));
    }
}