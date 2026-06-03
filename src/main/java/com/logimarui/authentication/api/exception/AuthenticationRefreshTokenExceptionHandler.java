package com.logimarui.authentication.api.exception;

import com.logimarui.authentication.core.application.exceptions.refreshtoken.RFInvalidException;
import com.logimarui.authentication.core.application.exceptions.refreshtoken.RFNotFoundException;
import com.logimarui.platform.web.exception.ApiErrorResponse;
import com.logimarui.platform.web.exception.ApiExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.logimarui.authentication")
public class AuthenticationRefreshTokenExceptionHandler extends ApiExceptionHandler {

    @ExceptionHandler(RFInvalidException.class)
    public ResponseEntity<ApiErrorResponse> handleRFInvalid(RFInvalidException exception) {
        return buildErrorResponse(
                "RF_INVALID",
                exception.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RFNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRFNotFound(RFNotFoundException exception) {
        return buildErrorResponse(
                "RF_NOT_FOUND",
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }
}