package com.logimarui.auth.core.domain.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException(String message) {
        super(message);
    }
}
