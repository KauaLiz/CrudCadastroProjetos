package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.customException.RecursoNaoEncontradoException;
import com.example.cadastroProjetos.customException.RegraNegocioException;
import com.example.cadastroProjetos.customException.ValidacaoException;
import com.example.cadastroProjetos.model.dto.MembroDto;
import com.example.cadastroProjetos.model.dto.ProjetoDto;
import com.example.cadastroProjetos.model.enums.Status;
import com.example.cadastroProjetos.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest

class ProjetoServiceTest {

    @Mock
    private ProjetoRepository repository;

    @Mock
    private MembroApiMockada membroApiMockada;

    @InjectMocks
    private ProjetoService projetoService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Não deve retornar exceção quando ID do membro for encontrado")
    void validarMembroEncontradoSucesso() {
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Funcionário"));

        assertDoesNotThrow(() ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Deve retornar exceção quando ID do membro não for encontrado")
    void validarMembroNaoEncontradoFalha() {
        when(membroApiMockada.consultarID(1L)).thenReturn(null);

        assertThrows(RecursoNaoEncontradoException.class, () ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Não deve retornar exceção pois o cargo está correto")
    void validarMembroCargoSucesso() {
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Funcionário"));

        assertDoesNotThrow(() ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Deve retornar exceção referente ao cargo estar diferente de funcionário")
    void validarMembroCargoFalha() {
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Gerente"));

        assertThrows(RegraNegocioException.class, () ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Não deve retornar exceção pois o membro não está em pelo menos 3 projetos")
    void validarLimiteTresMembrosPorProjetoSucesso(){
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Funcionário"));

        when(repository.contarProjetosMembroAtivo(1L, List.of(Status.ENCERRADO, Status.CANCELADO))).thenReturn(2L);

        assertDoesNotThrow(() ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Deve retornar exceção se membro já estiver em pelo menos 3 projetos")
    void validarLimiteTresMembrosPorProjetoFalha(){
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Funcionário"));

        when(repository.contarProjetosMembroAtivo(1L, List.of(Status.ENCERRADO, Status.CANCELADO))).thenReturn(4L);

        assertThrows(RegraNegocioException.class, () ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Não deve retornar exceção quando houver menos 10 membros no projeto")
    void validarLimiteDeMembrosNoProjetoSucesso(){
        List<Long> membrosId = List.of(1L,2L,3L,4L,5L,6L,7L);

        for(Long id : membrosId){
            when(membroApiMockada.consultarID(id)).thenReturn(new MembroDto("Membro" + id, "Funcionário"));

            when(repository.contarProjetosMembroAtivo(id, List.of(Status.ENCERRADO, Status.CANCELADO))).thenReturn(0L);
        }

        assertDoesNotThrow(() -> {
            projetoService.validarQuantidadeMembros(membrosId);
        });
    }

    @Test
    @DisplayName("Deve retornar exceção quando passar do limite de 10 membros")
    void validarLimiteDeMembrosNoProjetoFalha(){
         List<Long> membrosId = List.of(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L);

         assertThrows(ValidacaoException.class, () -> {
             projetoService.validarQuantidadeMembros(membrosId);
         });
    }

    @Test
    @DisplayName("Não deve retornar exceção caso não haja membros repetidos ao criar projeto")
    void validarMembrosRepetidosSucesso(){
        List<Long> membrosId = List.of(1L,2L,3L,4L,5L,6L,7L,8L);

        for(Long id : membrosId){
            when(membroApiMockada.consultarID(id)).thenReturn(new MembroDto("Membro" + id, "Funcionário"));

            when(repository.contarProjetosMembroAtivo(id, List.of(Status.ENCERRADO, Status.CANCELADO))).thenReturn(0L);
        }

        assertDoesNotThrow(() -> {
            projetoService.validarQuantidadeMembros(membrosId);
        });
    }

    @Test
    @DisplayName("Deve retornar exceção se houver membros repetidos ao criar projeto")
    void validarMembrosRepetidosFalha(){
        List<Long> membrosId = List.of(1L,2L,2L,4L,5L,6L,7L,8L,9L,10L,11L);

        assertThrows(ValidacaoException.class, () -> {
            projetoService.validarQuantidadeMembros(membrosId);
        });
    }

    @Test
    @DisplayName("Não deve retonar exceção quando houver um gerente existente")
    void validarGerenteEncontradoSucesso() {
        ProjetoDto projetoDto = new ProjetoDto(
                "ProjetoTeste",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                null,
                new BigDecimal("500"),
                "ProjetoTeste",
                1L,
                List.of(2L, 3L)
        );

        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Gerente"));

        assertDoesNotThrow(() ->{
            projetoService.validarGerente(projetoDto, List.of(2L, 3L));
        });
    }

    @Test
    @DisplayName("Deve retonar exceção quando não houver um gerente existente")
    void validarGerenteNaoEncontradoFalha() {
        ProjetoDto projetoDto = new ProjetoDto(
                "ProjetoTeste",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                null,
                new BigDecimal("500"),
                "ProjetoTeste",
                1L,
                List.of(2L, 3L)
        );

        when(membroApiMockada.consultarID(1L)).thenReturn(null);

        assertThrows(RecursoNaoEncontradoException.class, () ->{
            projetoService.validarGerente(projetoDto, null);
        });
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando um funcionário for atribuido como gerente de um projeto ")
    void membroNaoPodeSerGerente() {
        ProjetoDto projetoTeste = new ProjetoDto(
                "ProjetoTeste",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                null,
                new BigDecimal("500"),
                "ProjetoTeste",
                1L,
                null
        );

        when(membroApiMockada.consultarID(projetoTeste.gerenteId()))
                .thenReturn(new MembroDto("Kauã", "Funcionário"));

        assertThrows(RegraNegocioException.class, () -> {
           projetoService.validarGerente(projetoTeste, Collections.emptyList());
        });
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando um gerente for atribuido como um membro de um projeto ")
    void GerenteNaoPodeSerMembro() {
        Long gerenteId = 2L;
        List<Long> membrosId = List.of(1L,gerenteId,3L,4L);
        ProjetoDto projetoTeste = new ProjetoDto(
                "ProjetoTeste",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                null,
                new BigDecimal("500"),
                "ProjetoTeste",
                gerenteId,
                null
        );

        when(membroApiMockada.consultarID(gerenteId)).thenReturn(new MembroDto("Kauã", "Gerente"));

        assertThrows(RegraNegocioException.class, () -> {
            projetoService.validarGerente(projetoTeste, membrosId);
        });
    }

    @Test
    void validarQuantidadeInvalidaMembros() {

    }

    @Test
    void membroPodeSerAlocado() {
    }

    @Test
    void criar() {
    }

    @Test
    void retornarDadosRelatorio() {
    }

    @Test
    void transformarDto() {
    }

    @Test
    void mostrarProjetos() {
    }

    @Test
    void adicionarMembros() {
    }

    @Test
    void retornaProximoStatus() {
    }

    @Test
    void avancarStatus() {
    }

    @Test
    void cancelarProjeto() {
    }

    @Test
    void deletarProjeto() {
    }
}