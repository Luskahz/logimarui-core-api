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
public class PythonServiceProcessRunner implements ServiceProcessRunner {

    private static final String EXTRACTION_SERVICE_ID = "gerenciador-extracao";
    private static final String EXTRACTION_SHARE_S_ROOT = "\\\\192.168.0.213\\Files";

    private final ProcessTreeTerminator processTreeTerminator;
    private final WindowsPortInspector windowsPortInspector;
    private final ProcessPortAllocator processPortAllocator;

    @Override
    public boolean supports(ManagedService service) {
        return service.getType() == ServiceType.PYTHON;
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

            processBuilder.environment().put("PORT", String.valueOf(allocatedPort));
            String portEnvironmentVariable = service.getPortEnvironmentVariable();
            if (portEnvironmentVariable != null && !portEnvironmentVariable.isBlank()) {
                processBuilder.environment().put(
                        portEnvironmentVariable,
                        String.valueOf(allocatedPort)
                );
            }
            processBuilder.environment().put("SERVICE_HOST", "0.0.0.0");
            if (EXTRACTION_SERVICE_ID.equals(service.getId())) {
                processBuilder.environment().put("EXTRATOR_FORCE_UNC_DRIVE_FALLBACKS", "1");
                processBuilder.environment().putIfAbsent("EXTRATOR_DRIVE_S_ROOT", EXTRACTION_SHARE_S_ROOT);
            }

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
                    "Erro ao iniciar serviço Python: " + service.getId(),
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrompida ao aguardar porta do serviço.", exception);
        }
    }
}
