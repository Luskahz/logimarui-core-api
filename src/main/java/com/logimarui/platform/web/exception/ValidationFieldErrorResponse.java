package com.logimarui.platform.web.exception;

public record ValidationFieldErrorResponse(
        String field,
        String message,
        Object rejectedValue
) {
}