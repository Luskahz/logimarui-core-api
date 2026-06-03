package com.logimarui.authentication.core.domain.exception.session;

public class SessionMissingTtlException extends RuntimeException {
    public SessionMissingTtlException(String message) {
        super(message);
    }
}
