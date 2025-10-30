package com.example.demo.exception;

/**
 * Exceção para credenciais inválidas (login/senha incorretos)
 */
public class CredenciaisInvalidasException extends RuntimeException {
    
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
