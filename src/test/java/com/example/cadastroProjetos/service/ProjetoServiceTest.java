package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.customException.RecursoNaoEncontradoException;
import com.example.cadastroProjetos.customException.RegraNegocioException;
import com.example.cadastroProjetos.model.dto.MembroDto;
import com.example.cadastroProjetos.model.entity.ProjetoEntity;
import com.example.cadastroProjetos.model.enums.Status;
import com.example.cadastroProjetos.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("Deve retornar exceção referente a membro não encontrado")
    void validarMembroNaoEncontrado() {
        when(membroApiMockada.consultarID(1L)).thenReturn(null);

        assertThrows(RecursoNaoEncontradoException.class, () ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Deve retornar exceção referente ao cargo estar diferente de funcionário")
    void validarMembroCargoErrado() {
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Gerente"));

        assertThrows(RegraNegocioException.class, () ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    @DisplayName("Deve retornar exceção se membro já estiver em pelo menos 3 projetos")
    void validarMembroLimiteProjetos() {
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Funcionário"));

        when(repository.contarProjetosMembroAtivo(1L, List.of(Status.CANCELADO, Status.ENCERRADO))).thenReturn(3L);

        assertThrows(RegraNegocioException.class, () ->{
            projetoService.validarMembroIndividual(1L);
        });
    }

    @Test
    void validarGerenteEMembro() {
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