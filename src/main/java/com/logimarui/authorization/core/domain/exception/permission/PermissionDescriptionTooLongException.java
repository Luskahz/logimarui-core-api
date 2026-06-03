package com.logimarui.authorization.core.domain.exception.permission;

public class PermissionDescriptionTooLongException extends RuntimeException {

    public PermissionDescriptionTooLongException(int maxLength) {
        super("Permission description cannot exceed " + maxLength + " characters.");
    }
}