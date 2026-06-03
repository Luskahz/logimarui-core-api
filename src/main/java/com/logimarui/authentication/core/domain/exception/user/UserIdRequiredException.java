package com.logimarui.authentication.core.domain.exception.user;

public class UserIdRequiredException extends RuntimeException {
    public UserIdRequiredException(String message) {
        super(message);
    }
}
