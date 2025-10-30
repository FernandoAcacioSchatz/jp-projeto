package com.example.demo.exception;

/**
 * Exceção para token JWT inválido ou expirado
 */
public class TokenInvalidoException extends RuntimeException {
    
    public TokenInvalidoException(String mensagem) {
        super(mensagem);
    }
}
