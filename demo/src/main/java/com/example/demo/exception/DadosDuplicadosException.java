package com.example.demo.exception;

public class DadosDuplicadosException extends RuntimeException {
    
    public DadosDuplicadosException(String mensagem) {
        super(mensagem);
    }
}
