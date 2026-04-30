package com.logimarui.gateway.infra.process;

import com.logimarui.gateway.core.domain.model.ManagedService;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.domain.model.ServiceStatus;
import com.logimarui.gateway.core.domain.model.ServiceType;
import com.logimarui.gateway.core.port.ServiceProcessRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NodeServiceProcessRunner implements ServiceProcessRunner {

    private final ProcessTreeTerminator processTreeTerminator;
    private final WindowsPortInspector windowsPortInspector;
    private final ProcessPortAllocator processPortAllocator;

    @Override
    public boolean supports(ManagedService service) {
        return service.getType() == ServiceType.NODE;
    }

    @Override
    public ServiceRuntime start(ManagedService service) {
        Process process = null;
        int allocatedPort = processPortAllocator.allocateNearestAvailablePort(service.getPort());

        try {
            File logFile = resolveLogFile(service);

            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("cmd.exe", "/c", service.getStartCommand())
                    .directory(new File(service.getWorkingDirectory()))
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))
                    .redirectError(ProcessBuilder.Redirect.appendTo(logFile));

            injectPortEnvironment(service, allocatedPort, processBuilder);

            process = processBuilder.start();

            Long listenerPid = waitForListenerPid(allocatedPort);

            return new ServiceRuntime(
                    service.getId(),
                    allocatedPort,
                    process.pid(),
                    listenerPid,
                    ServiceStatus.RUNNING,
                    Instant.now()
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao iniciar serviço Node: " + service.getId(),
                    exception
            );
        } catch (RuntimeException exception) {
            if (process != null) {
                processTreeTerminator.terminate(process.pid());
            }

            throw exception;
        }
    }

    @Override
    public void stop(ManagedService service, ServiceRuntime runtime) {
        processTreeTerminator.terminate(runtime.getListenerPid());
        processTreeTerminator.terminate(runtime.getRootPid());
    }

    private Long waitForListenerPid(int port) {
        long deadline = System.currentTimeMillis() + 60_000;

        while (System.currentTimeMillis() < deadline) {
            Optional<Long> pid = windowsPortInspector.findListeningPidByPort(port);

            if (pid.isPresent()) {
                return pid.get();
            }

            sleep(300);
        }

        throw new IllegalStateException(
                "Serviço iniciou, mas não abriu a porta esperada dentro do timeout: " + port
        );
    }

    private File resolveLogFile(ManagedService service) {
        File logsDirectory = new File(".runtime", "logs");

        if (!logsDirectory.exists() && !logsDirectory.mkdirs()) {
            throw new IllegalStateException(
                    "Não foi possível criar diretório de logs: " + logsDirectory.getAbsolutePath()
            );
        }

        return new File(logsDirectory, service.getId() + ".log");
    }

    private void injectPortEnvironment(
            ManagedService service,
            int allocatedPort,
            ProcessBuilder processBuilder
    ) {
        String portEnvName = service.getPortEnvironmentVariable();

        if (portEnvName == null || portEnvName.isBlank()) {
            portEnvName = "PORT";
        }

        processBuilder.environment().put(portEnvName, String.valueOf(allocatedPort));
        processBuilder.environment().put("PORT", String.valueOf(allocatedPort));
        processBuilder.environment().put("SERVICE_HOST", "0.0.0.0");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrompida ao aguardar porta do serviço.", exception);
        }
    }
}
