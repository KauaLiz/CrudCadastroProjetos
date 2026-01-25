package com.example.cadastroProjetos.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.cadastroProjetos.customException.ValidacaoException;
import com.example.cadastroProjetos.model.entity.UserEntity;
import com.example.cadastroProjetos.repository.UserRepository;
import com.example.cadastroProjetos.service.AuthService;
import com.example.cadastroProjetos.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var token = recuperarToken(request);

        if (token != null) {
            var login = tokenService.validarToken(token);

            if (login != null) {
                UserDetails user = repository.findByLogin(login);

                if (user != null) {
                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
