package com.example.demo.exception;

public class NoSuchElementException extends RuntimeException{

    public NoSuchElementException(String mensagem) {
        super(mensagem);
    }

    public NoSuchElementException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

}
