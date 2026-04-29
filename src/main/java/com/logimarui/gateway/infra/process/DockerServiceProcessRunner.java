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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DockerServiceProcessRunner implements ServiceProcessRunner {

    private static final String INTEGRATION_NETWORK = "logimarui-n8n-evolution";
    private static final long DOCKER_SERVICE_START_TIMEOUT_MS = 120_000;
    private static final long PORT_CHECK_INTERVAL_MS = 500;

    private final WindowsPortInspector windowsPortInspector;
    private final ProcessPortAllocator processPortAllocator;
    private final DockerDesktopManager dockerDesktopManager;

    @Override
    public boolean supports(ManagedService service) {
        return service.getType() == ServiceType.DOCKER;
    }

    @Override
    public ServiceRuntime start(ManagedService service) {
        dockerDesktopManager.ensureDockerEngineRunning();
        ensureIntegrationNetworkExists();

        try {
            File logFile = resolveLogFile(service);

            stopDockerServiceIfConfigured(service, logFile);

            int allocatedPort = processPortAllocator.allocateNearestAvailablePort(service.getPort());

            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("cmd.exe", "/c", service.getStartCommand())
                    .directory(resolveWorkingDirectory(service))
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))
                    .redirectError(ProcessBuilder.Redirect.appendTo(logFile));

            injectPortEnvironment(service, allocatedPort, processBuilder);

            Process startProcess = processBuilder.start();
            int exitCode = startProcess.waitFor();

            if (exitCode != 0) {
                throw new IllegalStateException(
                        "Comando Docker de start falhou para serviço " + service.getId() +
                                ". ExitCode=" + exitCode +
                                ". Consulte o log: " + logFile.getAbsolutePath()
                );
            }

            Long listenerPid = waitForListenerPid(allocatedPort);

            return new ServiceRuntime(
                    service.getId(),
                    allocatedPort,
                    null,
                    listenerPid,
                    ServiceStatus.RUNNING,
                    Instant.now()
            );

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao iniciar serviço Docker: " + service.getId(),
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Thread interrompida ao iniciar serviço Docker: " + service.getId(),
                    exception
            );
        }
    }

    @Override
    public void stop(ManagedService service, ServiceRuntime runtime) {
        dockerDesktopManager.ensureDockerEngineRunning();

        String stopCommand = service.getStopCommand();

        if (stopCommand == null || stopCommand.isBlank()) {
            throw new IllegalStateException(
                    "Serviço Docker não possui stopCommand configurado: " + service.getId()
            );
        }

        try {
            File logFile = resolveLogFile(service);

            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("cmd.exe", "/c", stopCommand)
                    .directory(resolveWorkingDirectory(service))
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))
                    .redirectError(ProcessBuilder.Redirect.appendTo(logFile));

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IllegalStateException(
                        "Comando Docker de stop falhou para serviço " + service.getId() +
                                ". ExitCode=" + exitCode +
                                ". Consulte o log: " + logFile.getAbsolutePath()
                );
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao parar serviço Docker: " + service.getId(),
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Thread interrompida ao parar serviço Docker: " + service.getId(),
                    exception
            );
        }
    }

    private void stopDockerServiceIfConfigured(ManagedService service, File logFile) {
        String stopCommand = service.getStopCommand();

        if (stopCommand == null || stopCommand.isBlank()) {
            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("cmd.exe", "/c", stopCommand)
                    .directory(resolveWorkingDirectory(service))
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(logFile))
                    .redirectError(ProcessBuilder.Redirect.appendTo(logFile));

            Process process = processBuilder.start();
            process.waitFor();

        } catch (Exception ignored) {
            // Se não havia stack/container anterior, tudo bem.
            // O start logo abaixo é quem precisa ser validado de verdade.
        }
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
        processBuilder.environment().put("SERVICE_HOST", "127.0.0.1");
    }

    private void ensureIntegrationNetworkExists() {
        CommandResult inspectResult = runCommand(
                "docker network inspect " + INTEGRATION_NETWORK,
                new File(".")
        );

        if (inspectResult.exitCode() == 0) {
            return;
        }

        CommandResult createResult = runCommand(
                "docker network create " + INTEGRATION_NETWORK,
                new File(".")
        );

        if (createResult.exitCode() == 0) {
            return;
        }

        String output = createResult.output().toLowerCase();

        if (output.contains("already exists") || output.contains("já existe")) {
            return;
        }

        throw new IllegalStateException(
                "Não foi possível criar rede Docker: " + INTEGRATION_NETWORK +
                        "\nInspect output:\n" + inspectResult.output() +
                        "\nCreate output:\n" + createResult.output()
        );
    }

    private Long waitForListenerPid(int port) {
        long deadline = System.currentTimeMillis() + DOCKER_SERVICE_START_TIMEOUT_MS;

        while (System.currentTimeMillis() < deadline) {
            Optional<Long> pid = windowsPortInspector.findListeningPidByPort(port);

            if (pid.isPresent()) {
                return pid.get();
            }

            sleep(PORT_CHECK_INTERVAL_MS);
        }

        throw new IllegalStateException(
                "Serviço Docker iniciou, mas não abriu a porta esperada dentro do timeout: " + port
        );
    }

    private CommandResult runCommand(String command, File workingDirectory) {
        try {
            Process process = new ProcessBuilder()
                    .command("cmd.exe", "/c", command)
                    .directory(workingDirectory)
                    .redirectErrorStream(true)
                    .start();

            String output = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            int exitCode = process.waitFor();

            return new CommandResult(exitCode, output);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao executar comando: " + command,
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Thread interrompida ao executar comando: " + command,
                    exception
            );
        }
    }

    private File resolveWorkingDirectory(ManagedService service) {
        File workingDirectory = new File(service.getWorkingDirectory());

        if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
            throw new IllegalStateException(
                    "Diretório de trabalho inválido para serviço " + service.getId() +
                            ": " + workingDirectory.getAbsolutePath()
            );
        }

        return workingDirectory;
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
            throw new IllegalStateException(
                    "Thread interrompida ao aguardar serviço Docker.",
                    exception
            );
        }
    }

    private record CommandResult(int exitCode, String output) {
    }
}