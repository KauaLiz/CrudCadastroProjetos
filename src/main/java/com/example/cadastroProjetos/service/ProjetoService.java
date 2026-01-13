package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.customException.RecursoNaoEncontradoException;
import com.example.cadastroProjetos.customException.RegraNegocioException;
import com.example.cadastroProjetos.customException.ValidacaoException;
import com.example.cadastroProjetos.model.dto.MembroDto;
import com.example.cadastroProjetos.model.dto.ProjetoDto;
import com.example.cadastroProjetos.model.dto.ProjetoRequestDto;
import com.example.cadastroProjetos.model.dto.ProjetoResponseDto;
import com.example.cadastroProjetos.model.entity.ProjetoEntity;
import com.example.cadastroProjetos.model.enums.ClassificacaoRisco;
import com.example.cadastroProjetos.model.enums.Status;
import com.example.cadastroProjetos.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class ProjetoService {

    @Autowired
    ProjetoRepository repository;

    @Autowired
    MembroApiMockada membroApiMockada;

    private ProjetoEntity projeto;

    public void validarGerenteEMembro(ProjetoDto data, List<Long> membrosIds) {
        MembroDto gerente = membroApiMockada.consultarID(data.gerenteId());

        if (gerente == null) {
            throw new RecursoNaoEncontradoException("Gerente não encontrado");
        }

        if (!"gerente".equalsIgnoreCase(gerente.cargo())) {
            throw new RegraNegocioException("Cargo deve ser de Gerente");
        }

        if (membrosIds.contains(data.gerenteId())) {
            throw new RegraNegocioException("Gerente não pode ser membro");
        }

        if (membrosIds.isEmpty() || membrosIds.size() > 10) {
            throw new ValidacaoException("Quantidade inválida de membros");
        }

        //Duplicados
        if (membrosIds.size() != membrosIds.stream().distinct().count()) {
            throw new ValidacaoException("Há membros repetidos");
        }

        //Verifica cada membro
        for (Long id : membrosIds) {
            MembroDto membro = membroApiMockada.consultarID(id);

            if (membro == null) {
                throw new RecursoNaoEncontradoException("Membro do código " + id + " não encontrado");
            }

            if (!membro.cargo().equalsIgnoreCase("funcionário")) {
                throw new RegraNegocioException("Membro com cargo diferente de funcionário");
            }

            if (!membroPodeSerAlocado(id)) {
                throw new RegraNegocioException("Membro com o ID " + id + " já está em 3 ou mais projetos");
            }
        }
    }

    public boolean membroPodeSerAlocado(long membroId) {
        return repository.contarProjetosMembroAtivo(membroId, List.of(Status.ENCERRADO, Status.CANCELADO)) < 3;
    }

    public void criar(ProjetoDto data) {
        //Validação Gerente e Membro
        List<Long> membrosIds = data.membrosIds();
        validarGerenteEMembro(data, membrosIds);

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
        } else if ((data.orcamento().compareTo(new BigDecimal("100001")) > 0 && data.orcamento().compareTo(new BigDecimal("500000")) <= 0) || dias > 90 && dias < 180) {
            projeto.setRisco(ClassificacaoRisco.MEDIO);
        } else {
            projeto.setRisco(ClassificacaoRisco.ALTO);
        }

        repository.save(projeto);
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

        if (membrosAtuais.size() + membrosRequest.size() > 10) {
            throw new ValidacaoException("Quantidade de membros excede 10");
        }

        for(Long idRequest : membrosRequest){
            if(membrosAtuais.contains(idRequest)){
                throw new ValidacaoException("Membro com o ID " + idRequest +" já está incluso no projeto");
            }
        }

        if (membrosAtuais.contains(projeto.getGerenteID())) {
            throw new RegraNegocioException("Gerente não pode ser membro");
        }

        //Verifica cada membro
        for (Long idMembros : membrosRequest) {
            MembroDto membro = membroApiMockada.consultarID(idMembros);

            if (membro == null) {
                throw new RecursoNaoEncontradoException("Membro do código " + idMembros + " não encontrado");
            }

            if (!membro.cargo().equalsIgnoreCase("funcionário")) {
                throw new RegraNegocioException("Membro com cargo diferente de funcionário");
            }

            if (!membroPodeSerAlocado(idMembros)) {
                throw new RegraNegocioException("Membro com o ID " + idMembros + " já está em 3 ou mais projetos");
            }
        }
        membrosAtuais.addAll(membrosRequest);
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
        projeto.setStatus(proximoStatus);
        repository.save(projeto);
    }

    public void cancelarProjeto(long id){
        ProjetoEntity projeto = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Projeto com ID " + id + " não encontrado")
                );

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

