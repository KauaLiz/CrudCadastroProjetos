package com.example.cadastroProjetos.controller;

import com.example.cadastroProjetos.model.dto.ProjetoDto;
import com.example.cadastroProjetos.model.dto.ProjetoRequestDto;
import com.example.cadastroProjetos.model.dto.ProjetoResponseDto;
import com.example.cadastroProjetos.model.dto.RelatorioDto;
import com.example.cadastroProjetos.service.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/projeto", produces = {"application/json"})
@Tag(name = "open-api")
public class ProjetoController {

    @Autowired
    ProjetoService service;

    @Operation(summary = "Criar Projeto", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao criar projeto"),
    })
    @PostMapping(path = "/criar", consumes = {"application/json"})
    public ResponseEntity<String> criarProjeto(@RequestBody ProjetoDto data) {
        service.criar(data);
        return ResponseEntity.status(HttpStatus.CREATED).body("Projeto criado com sucesso");
    }

    @Operation(summary = "Buscar dados de todos os projetos", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar projetos"),
    })
    @GetMapping("/mostrarProjetos")
    public ResponseEntity<List<ProjetoResponseDto>> mostrarProjetos() {
        return ResponseEntity.ok(service.mostrarProjetos());
    }

    @Operation(summary = "Retornar dados para gerar relatório", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao gerar relatório"),
    })
    @GetMapping("/gerarRelatorio")
    public ResponseEntity<RelatorioDto> gerarRelatorio() {
        return ResponseEntity.ok(service.retornarDadosRelatorio());
    }

    @Operation(summary = "Associar membros ao projeto", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Membro Associado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao associar membro"),
    })
    @PatchMapping("/associar/{id}")
    public ResponseEntity<ProjetoResponseDto> associarMembro(@PathVariable long id, @RequestBody ProjetoRequestDto data) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.adicionarMembros(id, data));
    }

    @Operation(summary = "Atualizar para próximo status", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar projeto"),
    })
    @PatchMapping("/avancarStatus/{id}")
    public ResponseEntity<String> avancarStatus(@PathVariable long id) {
        service.avancarStatus(id);
        return ResponseEntity.status(200).body("Status atualiazdo com sucesso");
    }

    @Operation(summary = "Cancelar um projeto", method = "PATCH")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto cancelado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao cancelar projeto"),
    })
    @PatchMapping("/cancelar/{id}")
    public ResponseEntity<String> CancelarProjeto(@PathVariable long id) {
        service.cancelarProjeto(id);
        return ResponseEntity.status(200).body("Projeto cancelado com sucesso");
    }

    @Operation(summary = "Deletar um projeto", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto deletado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar projeto"),
    })
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletarProjeto(@PathVariable long id) {
        service.deletarProjeto(id);
        return ResponseEntity.status(200).body("Projeto deletado com sucesso");
    }
}

