package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.customException.RecursoNaoEncontradoException;
import com.example.cadastroProjetos.customException.RegraNegocioException;
import com.example.cadastroProjetos.model.dto.MembroDto;
import com.example.cadastroProjetos.model.dto.ProjetoDto;
import com.example.cadastroProjetos.model.entity.ProjetoEntity;
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
    @DisplayName("Deve retornar exceção quando ID do membro não for encontrado")
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
    void validarLimiteTresMembrosPorProjeto() {
        when(membroApiMockada.consultarID(1L)).thenReturn(new MembroDto("Kauã", "Funcionário"));

        when(repository.contarProjetosMembroAtivo(1L, List.of(Status.ENCERRADO, Status.CANCELADO))).thenReturn(4L);

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