package com.logimarui.authentication.api.exception;

import com.logimarui.authentication.core.application.exceptions.tokenhash.TokenInvalidException;
import com.logimarui.authentication.core.application.exceptions.tokenhash.TokenNotFoundException;
import com.logimarui.platform.web.exception.ApiErrorResponse;
import com.logimarui.platform.web.exception.ApiExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.logimarui.authentication")
public class AuthenticationTokenHashExceptionHandler extends ApiExceptionHandler {

    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ApiErrorResponse> handleTokenInvalid(TokenInvalidException exception) {
        return buildErrorResponse(
                "TOKEN_INVALID",
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTokenNotFound(TokenNotFoundException exception) {
        return buildErrorResponse(
                "TOKEN_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }
}