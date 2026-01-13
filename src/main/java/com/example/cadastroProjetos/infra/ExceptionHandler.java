package com.example.cadastroProjetos.infra;

import com.example.cadastroProjetos.customException.RecursoNaoEncontradoException;
import com.example.cadastroProjetos.customException.RegraNegocioException;
import com.example.cadastroProjetos.customException.ValidacaoException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<String> naoEncontrado(ValidacaoException ex){
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<String> regrasNegocio(RegraNegocioException ex){
        return ResponseEntity.status(409).body(ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<String> validacao(ValidacaoException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}