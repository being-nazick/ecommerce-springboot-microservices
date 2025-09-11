package com.billing.exception;

public class BillStatusException extends RuntimeException {
    
    public BillStatusException(String message) {
        super(message);
    }
    
    public BillStatusException(String message, Throwable cause) {
        super(message, cause);
    }
} 