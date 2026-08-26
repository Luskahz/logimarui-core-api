package com.logimarui.occurrence.api.exception;

import com.logimarui.occurrence.core.exception.OccurrenceNotFoundException;
import com.logimarui.occurrence.core.exception.OccurrenceStateConflictException;
import com.logimarui.occurrence.core.exception.ReturnContextNotFoundException;
import com.logimarui.platform.web.exception.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(basePackages = "com.logimarui.occurrence.api")
public class OccurrenceExceptionHandler {
    @ExceptionHandler(OccurrenceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(OccurrenceNotFoundException exception) {
        return response("OCCURRENCE_NOT_FOUND", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReturnContextNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleReturnContextNotFound(
            ReturnContextNotFoundException exception
    ) {
        return response("RETURN_CONTEXT_NOT_FOUND", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OccurrenceStateConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(OccurrenceStateConflictException exception) {
        return response("OCCURRENCE_STATE_CONFLICT", exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        return response("INVALID_OCCURRENCE_REQUEST", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponse> handleValidation(Exception exception) {
        return response("VALIDATION_ERROR", "Invalid occurrence request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unexpected occurrence API error", exception);
        return response("INTERNAL_ERROR", "Unexpected internal error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiErrorResponse> response(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(code, message, status));
    }
}
