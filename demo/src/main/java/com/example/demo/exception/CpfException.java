package com.example.demo.exception;

public class CpfException extends RuntimeException{

    public CpfException(String mensagem) {
        super(mensagem);
    }
    public CpfException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
