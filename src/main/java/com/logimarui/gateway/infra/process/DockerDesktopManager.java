package com.logimarui.gateway.infra.process;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class DockerDesktopManager {

    private static final String DEFAULT_DOCKER_DESKTOP_EXE =
            "C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe";

    private static final Duration START_TIMEOUT = Duration.ofSeconds(120);

    public void ensureDockerEngineRunning() {
        if (isDockerEngineRunning()) {
            return;
        }

        startDockerDesktop();
        waitUntilDockerEngineIsReady();
    }

    private boolean isDockerEngineRunning() {
        CommandResult result = runCommand("docker ps", new File("."));
        return result.exitCode() == 0;
    }

    private void startDockerDesktop() {
        File dockerDesktopExecutable = new File(DEFAULT_DOCKER_DESKTOP_EXE);

        if (!dockerDesktopExecutable.exists()) {
            throw new IllegalStateException(
                    "Docker Desktop não encontrado em: " + dockerDesktopExecutable.getAbsolutePath()
            );
        }

        try {
            new ProcessBuilder(dockerDesktopExecutable.getAbsolutePath())
                    .redirectErrorStream(true)
                    .start();
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao iniciar Docker Desktop.",
                    exception
            );
        }
    }

    private void waitUntilDockerEngineIsReady() {
        long deadline = System.currentTimeMillis() + START_TIMEOUT.toMillis();

        while (System.currentTimeMillis() < deadline) {
            if (isDockerEngineRunning()) {
                return;
            }

            sleep(2_000);
        }

        CommandResult lastResult = runCommand("docker ps", new File("."));

        throw new IllegalStateException(
                "Docker Desktop foi iniciado, mas o Docker Engine não ficou pronto dentro do timeout." +
                        "\nÚltima saída do docker ps:\n" + lastResult.output()
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Thread interrompida ao aguardar Docker Desktop.",
                    exception
            );
        }
    }

    private record CommandResult(int exitCode, String output) {
    }
}