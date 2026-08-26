package com.logimarui.occurrence.core.exception;

public class OccurrenceNotFoundException extends RuntimeException {
    public OccurrenceNotFoundException(Long id) {
        super("Occurrence not found: " + id);
    }
}
