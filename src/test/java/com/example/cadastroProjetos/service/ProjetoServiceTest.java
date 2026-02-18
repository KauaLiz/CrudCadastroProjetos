package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.customException.RecursoNaoEncontradoException;
import com.example.cadastroProjetos.customException.RegraNegocioException;
import com.example.cadastroProjetos.customException.ValidacaoException;
import com.example.cadastroProjetos.model.dto.MembroDto;
import com.example.cadastroProjetos.model.dto.ProjetoDto;
import com.example.cadastroProjetos.model.dto.ProjetoResponseDto;
import com.example.cadastroProjetos.model.dto.RelatorioDto;
import com.example.cadastroProjetos.model.entity.ProjetoEntity;
import com.example.cadastroProjetos.model.enums.ClassificacaoRisco;
import com.example.cadastroProjetos.model.enums.Status;
import com.example.cadastroProjetos.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
    @DisplayName("Não deve retornar uma exceção quando um gerente for atribuido corretamente")
    void validarCargoGerenteSucesso() {
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
                .thenReturn(new MembroDto("Kauã", "Gerente"));

        assertDoesNotThrow(() -> {
            projetoService.validarGerente(projetoTeste, Collections.emptyList());
        });
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando um funcionário for atribuido como gerente de um projeto ")
    void validarCargoGerenteFalha() {
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
    @DisplayName("Não deve retornar uma exceção quando um funcionário for atribuido corretamente")
    void validarCargoMembroSucesso() {
        Long idFuncionario = 1L;
        Long idGerente = 2L;
        List<Long> membrosId = List.of(1L,idFuncionario,3L,4L);
        ProjetoDto projetoTeste = new ProjetoDto(
                "ProjetoTeste",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                null,
                new BigDecimal("500"),
                "ProjetoTeste",
                idGerente,
                membrosId
        );

        when(membroApiMockada.consultarID(idGerente)).thenReturn(new MembroDto("Fulano", "Gerente"));
        when(membroApiMockada.consultarID(idFuncionario)).thenReturn(new MembroDto("Kauã", "Funcionário"));

        assertDoesNotThrow(() -> {
            projetoService.validarGerente(projetoTeste, membrosId);
        });
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando um gerente for atribuido como um membro de um projeto")
    void validarCargoMembroFalha() {
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
                membrosId
        );

        when(membroApiMockada.consultarID(gerenteId)).thenReturn(new MembroDto("Kauã", "Gerente"));

        assertThrows(RegraNegocioException.class, () -> {
            projetoService.validarGerente(projetoTeste, membrosId);
        });
    }

    @Test
    @DisplayName("Deve criar um projeto")
    void CriarProjetoComSucesso() {
        List<Long> membrosId = List.of(1L,2L,3L,4L,5L);
        Long gerenteId = 6L;
        ArgumentCaptor<ProjetoEntity> captor = ArgumentCaptor.forClass(ProjetoEntity.class);

        //Verificar Membros e Gerente
        when(membroApiMockada.consultarID(gerenteId)).thenReturn(new MembroDto("Kauã","Gerente"));

        for(Long id : membrosId){
            when(repository.contarProjetosMembroAtivo(id, List.of(Status.ENCERRADO, Status.CANCELADO))).thenReturn(0L);
            when(membroApiMockada.consultarID(id)).thenReturn(new MembroDto("Fulano" + id, "Funcionário"));
        }

        //Criação Projeto
        ProjetoDto projetoTeste = new ProjetoDto(
                "ProjetoTeste",
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                null,
                new BigDecimal("500"),
                "ProjetoTeste",
                gerenteId,
                membrosId
        );

        projetoService.criar(projetoTeste);

        verify(repository, Mockito.times(1)).save(captor.capture());
        ProjetoEntity entitySalvo = captor.getValue();

        assertEquals(ClassificacaoRisco.BAIXO, entitySalvo.getRisco());
        assertEquals(Status.EM_ANALISE, entitySalvo.getStatus());
    }

    @Test
    @DisplayName("Deve retornar os dados do relatório com sucesso")
    void retornarDadosRelatorioComSucesso() {
        ProjetoEntity projeto_teste1 = new ProjetoEntity();
        projeto_teste1.setNome("Teste1");
        projeto_teste1.setDataInicio(LocalDate.now());
        projeto_teste1.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        projeto_teste1.setDescricao("Descrição");
        projeto_teste1.setGerente(1L);
        projeto_teste1.setOrcamento(new BigDecimal("100"));
        projeto_teste1.setMembrosIds(List.of(2L,3L));
        projeto_teste1.setStatus(Status.EM_ANALISE);

        ProjetoEntity projeto_teste2 = new ProjetoEntity();
        projeto_teste2.setNome("Teste2");
        projeto_teste2.setDataInicio(LocalDate.now());
        projeto_teste2.setPrevisaoTermino(LocalDate.now().plusMonths(5));
        projeto_teste2.setDescricao("Descrição");
        projeto_teste2.setGerente(4L);
        projeto_teste2.setOrcamento(new BigDecimal("150"));
        projeto_teste2.setMembrosIds(List.of(5L,6L));
        projeto_teste2.setStatus(Status.EM_ANDAMENTO);

        ProjetoEntity projeto_teste3 = new ProjetoEntity();
        projeto_teste3.setNome("Teste1");
        projeto_teste3.setDataInicio(LocalDate.now());
        projeto_teste3.setPrevisaoTermino(LocalDate.now().plusMonths(7));
        projeto_teste3.setDataTermino(LocalDate.now().plusMonths(7));
        projeto_teste3.setDescricao("Descrição");
        projeto_teste3.setGerente(7L);
        projeto_teste3.setOrcamento(new BigDecimal("200"));
        projeto_teste3.setMembrosIds(List.of(8L,9L));
        projeto_teste3.setStatus(Status.ENCERRADO);

        List<ProjetoEntity> projetos = List.of(projeto_teste1, projeto_teste2, projeto_teste3);

        when(repository.findAll()).thenReturn(projetos);

        RelatorioDto data = projetoService.retornarDadosRelatorio();

        assertEquals(3,data.quantidadePorStatus().values().stream().reduce(0L, Long::sum));
        assertEquals(BigDecimal.valueOf(450),data.totalOrcadoPorStatus().values().stream().reduce(BigDecimal.valueOf(0),BigDecimal::add));
        assertEquals(7,data.mediaDuracaoProjetosEncerrados());
        assertEquals(6,data.totalMembrosUnicos());
    }

    @Test
    @DisplayName("Deve retornar relatorio com dados nulos quando for houver projetos existentes")
    void retornarDadosRelatorioComFalha() {
        when(repository.findAll()).thenReturn(List.of());

        RelatorioDto data = projetoService.retornarDadosRelatorio();

        assertEquals(0,data.quantidadePorStatus().values().stream().reduce(0L, Long::sum));
        assertEquals(BigDecimal.ZERO,data.totalOrcadoPorStatus().values().stream().reduce(BigDecimal.valueOf(0),BigDecimal::add));
        assertEquals(0,data.mediaDuracaoProjetosEncerrados());
        assertEquals(0,data.totalMembrosUnicos());
    }

    @Test
    @DisplayName("Deve retornar uma lista de todos os projetos")
    void mostrarProjetosSucesso() {
        ProjetoEntity projeto_teste1 = new ProjetoEntity();
        projeto_teste1.setNome("Teste1");
        projeto_teste1.setDataInicio(LocalDate.now());
        projeto_teste1.setPrevisaoTermino(LocalDate.now().plusMonths(2));
        projeto_teste1.setDescricao("Descrição");
        projeto_teste1.setGerente(1L);
        projeto_teste1.setOrcamento(new BigDecimal("100"));
        projeto_teste1.setMembrosIds(List.of(2L,3L));
        projeto_teste1.setStatus(Status.EM_ANALISE);

        ProjetoEntity projeto_teste2 = new ProjetoEntity();
        projeto_teste2.setNome("Teste2");
        projeto_teste2.setDataInicio(LocalDate.now());
        projeto_teste2.setPrevisaoTermino(LocalDate.now().plusMonths(5));
        projeto_teste2.setDescricao("Descrição");
        projeto_teste2.setGerente(4L);
        projeto_teste2.setOrcamento(new BigDecimal("150"));
        projeto_teste2.setMembrosIds(List.of(5L,6L));
        projeto_teste2.setStatus(Status.EM_ANDAMENTO);

        ProjetoEntity projeto_teste3 = new ProjetoEntity();
        projeto_teste3.setNome("Teste3");
        projeto_teste3.setDataInicio(LocalDate.now());
        projeto_teste3.setPrevisaoTermino(LocalDate.now().plusMonths(7));
        projeto_teste3.setDataTermino(LocalDate.now().plusMonths(7));
        projeto_teste3.setDescricao("Descrição");
        projeto_teste3.setGerente(7L);
        projeto_teste3.setOrcamento(new BigDecimal("200"));
        projeto_teste3.setMembrosIds(List.of(8L,9L));
        projeto_teste3.setStatus(Status.ENCERRADO);

        List<ProjetoEntity> projetos = List.of(projeto_teste1, projeto_teste2, projeto_teste3);
        when(repository.findAll()).thenReturn(projetos);

        List<ProjetoResponseDto> projetoDtos = projetoService.mostrarProjetos();

        assertEquals(3, projetoDtos.size());
        
        assertEquals("Teste1", projetoDtos.get(0).nome());
        assertEquals(Status.EM_ANALISE, projetoDtos.get(0).status());

        assertEquals("Teste2", projetoDtos.get(1).nome());
        assertEquals(Status.EM_ANDAMENTO, projetoDtos.get(1).status());

        assertEquals("Teste3", projetoDtos.get(2).nome());
        assertEquals(Status.ENCERRADO, projetoDtos.get(2).status());
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