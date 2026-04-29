package com.logimarui.gateway.infra.process;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Component
public class ProcessTreeTerminator {

    public void terminate(Long pid) {
        if (pid == null || pid <= 0) {
            return;
        }

        terminateWithProcessHandle(pid);
        terminateWithTaskKill(pid);
    }

    private void terminateWithProcessHandle(long pid) {
        ProcessHandle.of(pid).ifPresent(processHandle -> {
            processHandle.descendants()
                    .sorted(Comparator.comparingLong(ProcessHandle::pid).reversed())
                    .forEach(this::destroyProcess);

            destroyProcess(processHandle);
        });
    }

    private void destroyProcess(ProcessHandle processHandle) {
        try {
            processHandle.destroy();

            processHandle.onExit()
                    .completeOnTimeout(null, Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS)
                    .join();

            if (processHandle.isAlive()) {
                processHandle.destroyForcibly();
            }
        } catch (Exception ignored) {
            // fallback será o taskkill
        }
    }

    private void terminateWithTaskKill(long pid) {
        try {
            new ProcessBuilder(
                    "cmd.exe",
                    "/c",
                    "taskkill",
                    "/PID",
                    String.valueOf(pid),
                    "/T",
                    "/F"
            )
                    .redirectErrorStream(true)
                    .start()
                    .waitFor();
        } catch (Exception ignored) {
            // não propaga aqui para não quebrar shutdown da aplicação
        }
    }
}