package com.logimarui.gateway.infra.process;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class ProcessPortAllocator {

    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final int SEARCH_RADIUS = 100;

    private final WindowsPortInspector windowsPortInspector;

    public ProcessPortAllocator(WindowsPortInspector windowsPortInspector) {
        this.windowsPortInspector = windowsPortInspector;
    }

    public int allocateNearestAvailablePort(int preferredPort) {
        validatePort(preferredPort);

        if (isAvailable(preferredPort)) {
            return preferredPort;
        }

        Set<Integer> testedPorts = new HashSet<>();
        testedPorts.add(preferredPort);

        for (int distance = 1; distance <= SEARCH_RADIUS; distance++) {
            int upperPort = preferredPort + distance;
            if (isCandidateAvailable(upperPort, testedPorts)) {
                return upperPort;
            }

            int lowerPort = preferredPort - distance;
            if (isCandidateAvailable(lowerPort, testedPorts)) {
                return lowerPort;
            }
        }

        throw new IllegalStateException(
                "Nenhuma porta livre encontrada próxima da porta preferida: " + preferredPort
        );
    }

    public boolean isAvailable(int port) {
        validatePort(port);

        Optional<Long> listenerPid = windowsPortInspector.findListeningPidByPort(port);

        return listenerPid.isEmpty();
    }

    private boolean isCandidateAvailable(int port, Set<Integer> testedPorts) {
        if (port < MIN_PORT || port > MAX_PORT) {
            return false;
        }

        if (!testedPorts.add(port)) {
            return false;
        }

        return isAvailable(port);
    }

    private void validatePort(int port) {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException("Porta inválida: " + port);
        }
    }
}