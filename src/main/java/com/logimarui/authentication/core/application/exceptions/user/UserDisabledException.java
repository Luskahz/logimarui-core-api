package com.logimarui.authentication.core.application.exceptions.user;

public class UserDisabledException extends RuntimeException {
    public UserDisabledException(String message) {
        super(message);
    }
}
