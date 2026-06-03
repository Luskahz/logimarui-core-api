package com.logimarui.authentication.core.application.exceptions.user;

public class UserIdNotFoundException extends RuntimeException {

    public UserIdNotFoundException(String message) {
        super("User not found");
    }
}