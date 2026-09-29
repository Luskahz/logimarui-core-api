package com.logimarui.journey.api.v1;

import com.logimarui.platform.web.exception.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(basePackageClasses = JourneyController.class)
public class JourneyExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> invalid(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(
                "INVALID_JOURNEY_REQUEST", exception.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> invalidParameter(Exception exception) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(
                "INVALID_JOURNEY_REQUEST", "Invalid or missing journey parameter", HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> database(DataAccessException exception) {
        log.error("Journey READ procedure failed", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiErrorResponse.of(
                "JOURNEY_READ_ERROR", "Unable to read journey data", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
