package com.logimarui.authentication.core.domain.exception.user;

public class UserPasswordChangeRequiredException extends RuntimeException {
    public UserPasswordChangeRequiredException(String message) {
        super(message);
    }
}
