package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.customException.RecursoNaoEncontradoException;
import com.example.cadastroProjetos.customException.RegraNegocioException;
import com.example.cadastroProjetos.customException.ValidacaoException;
import com.example.cadastroProjetos.model.dto.*;
import com.example.cadastroProjetos.model.entity.ProjetoEntity;
import com.example.cadastroProjetos.model.enums.ClassificacaoRisco;
import com.example.cadastroProjetos.model.enums.Status;
import com.example.cadastroProjetos.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository repository;

    @Autowired
    private MembroApiMockada membroApiMockada;

    public void validarEquipe(ProjetoDto data, List<Long> membrosId){
        validarGerente(data, membrosId);
        validarQuantidadeMembros(membrosId);
    }

    public void validarMembroIndividual(Long membroId ) {
        MembroDto membro = membroApiMockada.consultarID(membroId);

        if (membro == null) throw new RecursoNaoEncontradoException("Membro do código " + membroId + " não encontrado");
        if (!membro.cargo().equalsIgnoreCase("funcionário")) throw new RegraNegocioException("Membro com cargo diferente de funcionário");
        if (!membroPodeSerAlocado(membroId)) throw new RegraNegocioException("Membro com o ID " + membroId + " já está em 3 ou mais projetos");
    }

    public void validarQuantidadeMembros(List<Long> membrosIds){
        if (membrosIds.isEmpty() || membrosIds.size() > 10) throw new ValidacaoException("Quantidade inválida de membros");
        if (membrosIds.size() != membrosIds.stream().distinct().count()) throw new ValidacaoException("Há membros repetidos");

        membrosIds.forEach(this::validarMembroIndividual);
    }

    public void validarGerente(ProjetoDto data, List<Long> membrosIds){
        MembroDto gerente = membroApiMockada.consultarID(data.gerenteId());

        if(gerente == null) throw new RecursoNaoEncontradoException("Gerente não encontrado");
        if(!gerente.cargo().equalsIgnoreCase("gerente")) throw new RegraNegocioException("Membro não pode ser um Gerente");
        if(membrosIds.contains(data.gerenteId())) throw new RegraNegocioException("Gerente não pode ser um membro");
    }

    public boolean membroPodeSerAlocado(long membroId) {
        return repository.contarProjetosMembroAtivo(membroId, List.of(Status.ENCERRADO, Status.CANCELADO)) < 3;
    }

    public void criar(ProjetoDto data) {
        //Validação Gerente e Membro
        List<Long> membrosIds = data.membrosIds();
        validarEquipe(data, membrosIds);

        //Criação Projeto
        ProjetoEntity projeto = new ProjetoEntity();
        projeto.setNome(data.nome());
        projeto.setDataInicio(data.dataInicio());
        projeto.setPrevisaoTermino(data.previsaoTermino());
        projeto.setDescricao(data.descricao());
        projeto.setDataTermino(data.dataTermino());
        projeto.setGerente(data.gerenteId());
        projeto.setOrcamento(data.orcamento());
        projeto.setMembrosIds(data.membrosIds());
        projeto.setStatus(Status.EM_ANALISE);

        //Validar Risco
        long dias = ChronoUnit.DAYS.between(data.dataInicio(), data.previsaoTermino());

        if (data.orcamento().compareTo(new BigDecimal("100000")) <= 0 && dias <= 90) {
            projeto.setRisco(ClassificacaoRisco.BAIXO);
        } else if ((data.orcamento().compareTo(new BigDecimal("100001")) > 0 && data.orcamento().compareTo(new BigDecimal("500000")) <= 0) || (dias > 90 && dias < 180)) {
            projeto.setRisco(ClassificacaoRisco.MEDIO);
        } else {
            projeto.setRisco(ClassificacaoRisco.ALTO);
        }

        repository.save(projeto);
    }

    private Map<Status,Long> qtdProjetosStatus(List<ProjetoEntity> projetos){
        Map<Status, Long> qtdProjetosStatus = new HashMap<>();
        for (ProjetoEntity projeto : projetos){
            Status status = projeto.getStatus();

            if(!qtdProjetosStatus.containsKey(status)){
                qtdProjetosStatus.put(status, 0L);
            }

            qtdProjetosStatus.put(status, qtdProjetosStatus.get(status) + 1);
        }
        return qtdProjetosStatus;
    }

    private Map<Status,BigDecimal> totalOrcadoStatus(List<ProjetoEntity> projetos){
        Map<Status, BigDecimal> totalOrcadoStatus = new HashMap<>();
        BigDecimal valorAtual = null;
        for (ProjetoEntity projeto : projetos){
            Status status = projeto.getStatus();
            valorAtual = totalOrcadoStatus.getOrDefault(status,BigDecimal.ZERO);

            totalOrcadoStatus.put(status, valorAtual.add(projeto.getOrcamento()));
        }
        return totalOrcadoStatus;
    }

    private Long mediaDuracaoProjetos(List<ProjetoEntity> projetos){
        Long qtdProjetos = 0L;
        Long meses = 0L;

        for(ProjetoEntity projeto : projetos){
            Status status = projeto.getStatus();

            if(status == Status.ENCERRADO){
                Long difTempo = ChronoUnit.MONTHS.between(projeto.getDataInicio(), projeto.getDataTermino());
                meses = meses + difTempo;
                qtdProjetos++;
            }
        }

        Long mediaDuracaoProjetos = 0L;
        if(qtdProjetos > 0) {
            mediaDuracaoProjetos = meses / qtdProjetos;
        } else {
            mediaDuracaoProjetos = 0L;
        }
        return mediaDuracaoProjetos;
    }

    private Long qtdMembrosUnicos(List<ProjetoEntity> projetos){

        Set<Long> qtdMembroUnico = new HashSet<>();

        for(ProjetoEntity projeto: projetos){
            for(Long membroId : projeto.getMembrosIds()){
                qtdMembroUnico.add(membroId);
            }
        }
        long qtdMembrosUnicos = qtdMembroUnico.size();
        return qtdMembrosUnicos;
    }

    public RelatorioDto retornarDadosRelatorio(){
        List<ProjetoEntity> projetos = repository.findAll();
        RelatorioDto relatorioDto = new RelatorioDto(
                qtdProjetosStatus(projetos),
                totalOrcadoStatus(projetos),
                mediaDuracaoProjetos(projetos),
                qtdMembrosUnicos(projetos)
        );

        return relatorioDto;
    }

    public ProjetoResponseDto transformarDto(ProjetoEntity projeto) {
        return new ProjetoResponseDto(
                projeto.getNome(),
                projeto.getDataInicio(),
                projeto.getPrevisaoTermino(),
                projeto.getDataTermino(),
                projeto.getOrcamento(),
                projeto.getDescricao(),
                projeto.getGerenteID(),
                projeto.getMembrosIds(),
                projeto.getStatus(),
                projeto.getRisco()
        );
    }

    public List<ProjetoResponseDto> mostrarProjetos() {
        return repository.findAll().stream()
                .map(this::transformarDto)
                .toList();
    }

    @Transactional
    public ProjetoResponseDto adicionarMembros(long id, ProjetoRequestDto data) {
        ProjetoEntity projeto = repository.findById(id).
                orElseThrow(() ->
                        new RecursoNaoEncontradoException("Projeto com ID " + id + " não encontrado")
                );

        List<Long> membrosAtuais = projeto.getMembrosIds();
        List<Long> membrosRequest = data.membrosIds();

        if(membrosRequest == null || membrosRequest.isEmpty()){
            throw new ValidacaoException("Lista de novos membros é obrigatória");
        }

        if (membrosAtuais.size() + membrosRequest.size() > 10) throw new ValidacaoException("Quantidade de membros excede 10");

        for(Long idRequest : membrosRequest){
            if(membrosAtuais.contains(idRequest)) throw new ValidacaoException("Membro com o ID " + idRequest +" já está incluso no projeto");
        }

        membrosRequest.forEach(this::validarMembroIndividual);

        membrosAtuais.addAll(membrosRequest);
        projeto.setMembrosIds(membrosAtuais);
        return transformarDto(projeto);
    }

    public Status retornaProximoStatus(Status status){
        switch(status){
            case EM_ANALISE:
                return Status.ANALISE_REALIZADA;
            case ANALISE_REALIZADA:
                return Status.ANALISE_APROVADA;
            case ANALISE_APROVADA:
                return Status.INICIADO;
            case INICIADO:
                return Status.PLANEJADO;
            case PLANEJADO:
                return Status.EM_ANDAMENTO;
            case EM_ANDAMENTO:
                return Status.ENCERRADO;
            default:
                throw new RegraNegocioException("Não é possível mudar o status de um projeto "+ status.getDescricao().toLowerCase());
        }
    }

    public void avancarStatus(long id){
        ProjetoEntity projeto = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Projeto com ID " + id + " não encontrado")
                );
        Status proximoStatus = retornaProximoStatus(projeto.getStatus());

        if(proximoStatus == Status.ENCERRADO){
            projeto.setDataTermino(LocalDate.now());
        }

        projeto.setStatus(proximoStatus);
        repository.save(projeto);
    }

    public void cancelarProjeto(long id){
        ProjetoEntity projeto = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Projeto com ID " + id + " não encontrado")
                );

        if(projeto.getStatus() == Status.ENCERRADO || projeto.getStatus() ==  Status.CANCELADO){
            throw new ValidacaoException("Projeto já está com status de " + projeto.getStatus());
        }

        projeto.setStatus(Status.CANCELADO);
        repository.save(projeto);
    }

    public void deletarProjeto(long id){
        ProjetoEntity projeto = repository.findById(id)
                .orElseThrow(() ->
                        new RegraNegocioException("Projeto com ID " + id + " não encontrado")
                );

        repository.delete(projeto);
    }
}
