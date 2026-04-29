package com.logimarui.gateway.infra.process;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

@Component
public class WindowsPortInspector {

    public Optional<Long> findListeningPidByPort(int port) {
        try {
            Process process = new ProcessBuilder(
                    "cmd.exe",
                    "/c",
                    "netstat -ano | findstr :" + port
            )
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            )) {
                String line;

                while ((line = reader.readLine()) != null) {
                    Optional<Long> pid = parseListeningPid(line, port);

                    if (pid.isPresent()) {
                        process.waitFor();
                        return pid;
                    }
                }
            }

            process.waitFor();
            return Optional.empty();

        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao inspecionar porta: " + port, exception);
        }
    }

    private Optional<Long> parseListeningPid(String line, int port) {
        String normalized = line.trim().replaceAll("\\s+", " ");

        if (!normalized.contains("LISTENING")) {
            return Optional.empty();
        }

        if (!normalized.contains(":" + port)) {
            return Optional.empty();
        }

        String[] parts = normalized.split(" ");

        if (parts.length < 5) {
            return Optional.empty();
        }

        String pidText = parts[parts.length - 1];

        try {
            return Optional.of(Long.parseLong(pidText));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}