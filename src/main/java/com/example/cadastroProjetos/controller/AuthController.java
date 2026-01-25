package com.example.cadastroProjetos.controller;

import com.example.cadastroProjetos.model.dto.AuthenticationDto;
import com.example.cadastroProjetos.model.dto.LoginResponseDto;
import com.example.cadastroProjetos.model.dto.RegisterDto;
import com.example.cadastroProjetos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@Tag(name = "Registrar e Logar Usuários")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realizar com usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da Requição Inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor"),
    })
    public ResponseEntity<LoginResponseDto> login(@RequestBody AuthenticationDto data){
         return (authService.login(data));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro realizado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da Requição Inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor"),
    })
    public ResponseEntity<Void> register(@RequestBody RegisterDto data){
        authService.register(data);
        return ResponseEntity.status(200).build();
    }
}
