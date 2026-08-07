package com.logimarui.gateway.core.domain.model;

import lombok.Getter;

@Getter
public class ManagedService {

    private final String id;
    private final String pathPrefix;
    private final ServiceType type;
    private final String workingDirectory;
    private final String startCommand;
    private final String stopCommand;
    private final int port;
    private final boolean requiresAuthentication;
    private final String portEnvironmentVariable;
    private final boolean startOnBoot;
    private final boolean enabled;

    public ManagedService(
            String id,
            String pathPrefix,
            ServiceType type,
            String workingDirectory,
            String startCommand,
            int port,
            boolean requiresAuthentication
    ) {
        this(
                id,
                pathPrefix,
                type,
                workingDirectory,
                startCommand,
                null,
                port,
                requiresAuthentication,
                "PORT",
                true,
                true
            );
    }

    public ManagedService(
            String id,
            String pathPrefix,
            ServiceType type,
            String workingDirectory,
            String startCommand,
            String stopCommand,
            int port,
            boolean requiresAuthentication,
            String portEnvironmentVariable
    ) {
        this(
                id,
                pathPrefix,
                type,
                workingDirectory,
                startCommand,
                stopCommand,
                port,
                requiresAuthentication,
                portEnvironmentVariable,
                true,
                true
        );
    }

    public ManagedService(
            String id,
            String pathPrefix,
            ServiceType type,
            String workingDirectory,
            String startCommand,
            int port,
            boolean requiresAuthentication,
            boolean startOnBoot
    ) {
        this(
                id,
                pathPrefix,
                type,
                workingDirectory,
                startCommand,
                null,
                port,
                requiresAuthentication,
                "PORT",
                startOnBoot,
                true
        );
    }

    public ManagedService(
            String id,
            String pathPrefix,
            ServiceType type,
            String workingDirectory,
            String startCommand,
            String stopCommand,
            int port,
            boolean requiresAuthentication,
            String portEnvironmentVariable,
            boolean startOnBoot
    ) {
        this(
                id,
                pathPrefix,
                type,
                workingDirectory,
                startCommand,
                stopCommand,
                port,
                requiresAuthentication,
                portEnvironmentVariable,
                startOnBoot,
                true
        );
    }

    public ManagedService(
            String id,
            String pathPrefix,
            ServiceType type,
            String workingDirectory,
            String startCommand,
            String stopCommand,
            int port,
            boolean requiresAuthentication,
            String portEnvironmentVariable,
            boolean startOnBoot,
            boolean enabled
    ) {
        this.id = id;
        this.pathPrefix = pathPrefix;
        this.type = type;
        this.workingDirectory = workingDirectory;
        this.startCommand = startCommand;
        this.stopCommand = stopCommand;
        this.port = port;
        this.requiresAuthentication = requiresAuthentication;
        this.portEnvironmentVariable = portEnvironmentVariable;
        this.startOnBoot = startOnBoot;
        this.enabled = enabled;
    }
}
