package com.example.demo.exception;

public abstract class BaseBusinessException extends RuntimeException {
    
    public BaseBusinessException(String message) {
        super(message);
    }
    
    public BaseBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
