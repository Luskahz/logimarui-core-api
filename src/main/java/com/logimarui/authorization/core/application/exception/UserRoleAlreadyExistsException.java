package com.logimarui.authorization.core.application.exception;

public class UserRoleAlreadyExistsException extends RuntimeException {

    public UserRoleAlreadyExistsException(
            Long userId,
            Long roleId
    ) {
        super("Role already assigned to user. User id: "
                + userId
                + ", role id: "
                + roleId);
    }
}