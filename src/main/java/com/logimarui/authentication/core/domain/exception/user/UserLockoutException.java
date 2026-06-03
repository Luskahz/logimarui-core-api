package com.logimarui.authentication.core.domain.exception.user;

public class UserLockoutException extends RuntimeException {
    public UserLockoutException(String message) {
        super(message);
    }
}
