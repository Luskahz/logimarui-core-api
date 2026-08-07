package com.logimarui.gateway.core.domain.model;

import lombok.Getter;

@Getter
public class ServiceRoute {

    private static final String BACKEND_API_ROOT = "/api";
    private static final String CORE_API_PREFIX = "/api/v1";

    private final String id;
    private final String pathPrefix;
    private final String targetUri;
    private final boolean requiresAuthentication;

    public ServiceRoute(
            String id,
            String pathPrefix,
            String targetUri,
            boolean requiresAuthentication
    ) {
        validatePathPrefix(pathPrefix);

        this.id = id;
        this.pathPrefix = pathPrefix;
        this.targetUri = targetUri;
        this.requiresAuthentication = requiresAuthentication;
    }

    private static void validatePathPrefix(String pathPrefix) {
        String normalizedPathPrefix = removeTrailingSlashes(pathPrefix);

        if (BACKEND_API_ROOT.equals(normalizedPathPrefix)
                || CORE_API_PREFIX.equals(normalizedPathPrefix)
                || normalizedPathPrefix != null
                && normalizedPathPrefix.startsWith(CORE_API_PREFIX + "/")) {
            throw new IllegalArgumentException(
                    "ServiceRoute nao pode ocupar o namespace reservado " + pathPrefix
            );
        }
    }

    private static String removeTrailingSlashes(String pathPrefix) {
        if (pathPrefix == null) {
            return null;
        }

        int endIndex = pathPrefix.length();
        while (endIndex > 1 && pathPrefix.charAt(endIndex - 1) == '/') {
            endIndex--;
        }

        return pathPrefix.substring(0, endIndex);
    }
}
