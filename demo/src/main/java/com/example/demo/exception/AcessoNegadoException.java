package com.example.demo.exception;

/**
 * Exceção para acesso negado (sem permissão)
 */
public class AcessoNegadoException extends RuntimeException {
    
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
