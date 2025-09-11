package com.billing.exception;

public class InvalidBillDataException extends RuntimeException {
    
    public InvalidBillDataException(String message) {
        super(message);
    }
    
    public InvalidBillDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 