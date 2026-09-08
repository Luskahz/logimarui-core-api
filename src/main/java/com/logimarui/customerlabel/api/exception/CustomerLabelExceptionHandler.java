package com.logimarui.customerlabel.api.exception;

import com.logimarui.customerlabel.core.exception.CustomerLabelNotFoundException;
import com.logimarui.platform.web.exception.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(basePackages = "com.logimarui.customerlabel.api")
public class CustomerLabelExceptionHandler {
    @ExceptionHandler(CustomerLabelNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(CustomerLabelNotFoundException exception) {
        return response("CUSTOMER_LABEL_NOT_FOUND", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleValidation(Exception exception) {
        return response("VALIDATION_ERROR", "Invalid customer id", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected customer label API error", exception);
        return response("INTERNAL_ERROR", "Unexpected internal error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiErrorResponse> response(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(code, message, status));
    }
}
