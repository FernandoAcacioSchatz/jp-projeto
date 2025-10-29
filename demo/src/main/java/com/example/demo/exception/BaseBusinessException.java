package com.example.demo.exception;

/**
 * Classe base para todas as exceções de negócio da aplicação
 */
public abstract class BaseBusinessException extends RuntimeException {
    
    public BaseBusinessException(String message) {
        super(message);
    }
    
    public BaseBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
