package com.logimarui.authentication.core.domain.exception.session;

public class SessionMissingIdException extends RuntimeException {
    public SessionMissingIdException(String message) {
        super(message);
    }
}
