package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.model.dto.AuthenticationDto;
import com.example.cadastroProjetos.model.dto.LoginResponseDto;
import com.example.cadastroProjetos.model.dto.RegisterDto;
import com.example.cadastroProjetos.model.entity.UserEntity;
import com.example.cadastroProjetos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AutorizacaoService autorizacaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<LoginResponseDto> login(AuthenticationDto data){
        var username = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(username);


        var token = tokenService.gerarToken((UserEntity) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    public ResponseEntity<Void> register(RegisterDto data){
        if(autorizacaoService.loadUserByUsername(data.login()) != null) return ResponseEntity.badRequest().build();

        String senhaCriptografa = passwordEncoder.encode(data.senha());
        UserEntity user = new UserEntity(data.login(), senhaCriptografa, data.role());

        this.repository.save(user);
        return ResponseEntity.ok().build();
    }
}
