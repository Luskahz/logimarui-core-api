package com.logimarui.gateway.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServiceRoute {

    private final String id;
    private final String pathPrefix;
    private final String targetUri;
    private final boolean requiresAuthentication;
}