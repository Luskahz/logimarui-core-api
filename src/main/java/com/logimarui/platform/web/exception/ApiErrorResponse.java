package com.logimarui.platform.web.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String code,
        String message,
        int status,
        LocalDateTime timestamp
) {
    public static ApiErrorResponse of(
            String code,
            String message,
            HttpStatus status
    ) {
        return new ApiErrorResponse(
                code,
                message,
                status.value(),
                LocalDateTime.now()
        );
    }
}