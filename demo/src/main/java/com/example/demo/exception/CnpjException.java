package com.example.demo.exception;

public class CnpjException extends RuntimeException {

    public CnpjException(String message) {
        super(message);
    }

    public CnpjException(String message, Throwable cause) {
        super(message, cause);
    }

}
