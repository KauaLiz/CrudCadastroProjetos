package com.example.cadastroProjetos.customException;

public class RecursoNaoEncontradoException extends RuntimeException{
    public RecursoNaoEncontradoException(String message){
        super(message);
    }
}
