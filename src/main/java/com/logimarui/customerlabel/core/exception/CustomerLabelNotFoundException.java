package com.logimarui.customerlabel.core.exception;

public class CustomerLabelNotFoundException extends RuntimeException {
    public CustomerLabelNotFoundException(Long customerId) {
        super("Customer label not found for customer " + customerId);
    }
}
