package com.logimarui.gateway.infra.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.logimarui.gateway.core.domain.model.ServiceRuntime;
import com.logimarui.gateway.core.port.ServiceRuntimeRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class FileServiceRuntimeRepository implements ServiceRuntimeRepository {

    private final Path pidDirectory = Paths.get(".runtime", "pids");

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public FileServiceRuntimeRepository() {
        createPidDirectoryIfNeeded();
    }

    @Override
    public void save(ServiceRuntime runtime) {
        Path file = resolvePidFile(runtime.getServiceId());

        try {
            objectMapper.writeValue(file.toFile(), runtime);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao salvar runtime do serviço: " + runtime.getServiceId(),
                    exception
            );
        }
    }

    @Override
    public Optional<ServiceRuntime> findByServiceId(String serviceId) {
        Path file = resolvePidFile(serviceId);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(file.toFile(), ServiceRuntime.class));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao ler runtime do serviço: " + serviceId,
                    exception
            );
        }
    }

    @Override
    public List<ServiceRuntime> findAll() {
        createPidDirectoryIfNeeded();

        List<ServiceRuntime> runtimes = new ArrayList<>();

        try (DirectoryStream<Path> files = Files.newDirectoryStream(pidDirectory, "*.pid")) {
            for (Path file : files) {
                try {
                    runtimes.add(objectMapper.readValue(file.toFile(), ServiceRuntime.class));
                } catch (IOException exception) {
                    throw new IllegalStateException(
                            "Erro ao ler runtime salvo no arquivo: " + file.toAbsolutePath(),
                            exception
                    );
                }
            }

            return runtimes;
        } catch (IOException exception) {
            throw new IllegalStateException("Erro ao listar runtimes salvos em arquivo.", exception);
        }
    }

    @Override
    public void deleteByServiceId(String serviceId) {
        Path file = resolvePidFile(serviceId);

        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao remover runtime do serviço: " + serviceId,
                    exception
            );
        }
    }

    private Path resolvePidFile(String serviceId) {
        return pidDirectory.resolve(serviceId + ".pid");
    }

    private void createPidDirectoryIfNeeded() {
        try {
            Files.createDirectories(pidDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Erro ao criar diretório de PID: " + pidDirectory, exception);
        }
    }
}