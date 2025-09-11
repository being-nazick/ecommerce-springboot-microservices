package com.billing.exception;

public class InvalidPaymentDataException extends RuntimeException {
    
    public InvalidPaymentDataException(String message) {
        super(message);
    }
    
    public InvalidPaymentDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 