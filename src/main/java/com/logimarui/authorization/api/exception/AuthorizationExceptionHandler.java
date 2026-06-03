package com.logimarui.authorization.api.exception;

import com.logimarui.authentication.core.application.exceptions.user.UserIdNotFoundException;
import com.logimarui.authorization.core.application.exception.*;
import com.logimarui.authorization.core.domain.exception.role.DeletedRoleCannotBeChangedException;
import com.logimarui.authorization.core.domain.exception.role.InactiveRoleCannotBeUsedException;
import com.logimarui.platform.web.exception.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.logimarui.authorization.api")
public class AuthorizationExceptionHandler {

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePermissionNotFound(
            PermissionNotFoundException exception
    ) {
        return buildErrorResponse(
                "PERMISSION_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleNotFound(
            RoleNotFoundException exception
    ) {
        return buildErrorResponse(
                "ROLE_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleAlreadyExists(
            RoleAlreadyExistsException exception
    ) {
        return buildErrorResponse(
                "ROLE_ALREADY_EXISTS",
                exception.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(RolePermissionAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRolePermissionAlreadyExists(
            RolePermissionAlreadyExistsException exception
    ) {
        return buildErrorResponse(
                "ROLE_PERMISSION_ALREADY_EXISTS",
                exception.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(RolePermissionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRolePermissionNotFound(
            RolePermissionNotFoundException exception
    ) {
        return buildErrorResponse(
                "ROLE_PERMISSION_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UserRoleAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserRoleAlreadyExists(
            UserRoleAlreadyExistsException exception
    ) {
        return buildErrorResponse(
                "USER_ROLE_ALREADY_EXISTS",
                exception.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserRoleNotFound(
            UserRoleNotFoundException exception
    ) {
        return buildErrorResponse(
                "USER_ROLE_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserIdNotFoundException exception
    ) {
        return buildErrorResponse(
                "USER_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DeletedRoleCannotBeChangedException.class)
    public ResponseEntity<ApiErrorResponse> handleDeletedRoleCannotBeChanged(
            DeletedRoleCannotBeChangedException exception
    ) {
        return buildErrorResponse(
                "DELETED_ROLE_CANNOT_BE_CHANGED",
                exception.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InactiveRoleCannotBeUsedException.class)
    public ResponseEntity<ApiErrorResponse> handleInactiveRoleCannotBeUsed(
            InactiveRoleCannotBeUsedException exception
    ) {
        return buildErrorResponse(
                "INACTIVE_ROLE_CANNOT_BE_USED",
                exception.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            String code,
            String message,
            HttpStatus status
    ) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.of(
                        code,
                        message,
                        status
                ));
    }
}