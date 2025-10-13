package com.example.demo.exception;

public class EmailException  extends RuntimeException{

    public EmailException(String mensagem) {
        super(mensagem);
    }
    public EmailException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

}
