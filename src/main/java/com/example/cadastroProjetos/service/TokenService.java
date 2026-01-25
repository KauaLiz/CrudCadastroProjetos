package com.example.cadastroProjetos.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.cadastroProjetos.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret_key;

    public String gerarToken(UserEntity user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret_key);
            return JWT.create()
                    .withIssuer("Criar e Editar Projetos")
                    .withSubject(user.getUsername())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        }catch(JWTCreationException exception){
            return null;
        }
    }

    public String validarToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret_key);
            return JWT.require(algorithm)
                    .withIssuer("Criar e Editar Projetos")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch(JWTVerificationException exception){
            return null;
        }
    }

    private Instant gerarDataExpiracao(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
