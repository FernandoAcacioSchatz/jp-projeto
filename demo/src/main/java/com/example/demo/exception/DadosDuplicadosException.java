package com.example.demo.exception;

/**
 * Exceção para dados duplicados (email, CPF, CNPJ já cadastrados)
 */
public class DadosDuplicadosException extends RuntimeException {
    
    public DadosDuplicadosException(String mensagem) {
        super(mensagem);
    }
}
