package com.logimarui.gateway.core.domain.model;

import lombok.Getter;

@Getter
public class ServiceRoute {

    private static final String BACKEND_API_ROOT = "/api";
    private static final String CORE_API_V1_PREFIX = "/api/v1";
    private static final String CORE_API_V2_PREFIX = "/api/v2";

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
                || isCoreApiNamespace(normalizedPathPrefix, CORE_API_V1_PREFIX)
                || isCoreApiNamespace(normalizedPathPrefix, CORE_API_V2_PREFIX)) {
            throw new IllegalArgumentException(
                    "ServiceRoute nao pode ocupar o namespace reservado " + pathPrefix
            );
        }
    }

    private static boolean isCoreApiNamespace(String pathPrefix, String apiPrefix) {
        return apiPrefix.equals(pathPrefix)
                || pathPrefix != null && pathPrefix.startsWith(apiPrefix + "/");
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
