package com.logimarui.authentication.core.domain.exception.session;

public class SessionInvalidActiveStateException extends RuntimeException {
    public SessionInvalidActiveStateException(String message) {
        super(message);
    }
}
