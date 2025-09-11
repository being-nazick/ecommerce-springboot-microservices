package com.billing.exception;

public class BillNotFoundException extends RuntimeException {
    
    public BillNotFoundException(String message) {
        super(message);
    }
    
    public BillNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 