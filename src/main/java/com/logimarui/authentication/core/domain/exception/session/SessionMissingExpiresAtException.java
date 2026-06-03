package com.logimarui.authentication.core.domain.exception.session;

public class SessionMissingExpiresAtException extends RuntimeException {
    public SessionMissingExpiresAtException(String message) {
        super(message);
    }
}
