package com.logimarui.occurrence.core.exception;

public class ReturnContextNotFoundException extends RuntimeException {
    public ReturnContextNotFoundException(Long customerId, Long invoiceNumber) {
        super("Return context not found for customer " + customerId + " and invoice " + invoiceNumber);
    }
}
