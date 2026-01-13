package com.example.cadastroProjetos.model.entity;

import com.example.cadastroProjetos.model.enums.ClassificacaoRisco;
import com.example.cadastroProjetos.model.enums.Status;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "projeto")
@Entity(name = "projeto")
public class ProjetoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Bigint no banco

    private String nome; //VARCHAR

    private LocalDate dataInicio; //Date

    private LocalDate previsaoTermino; //Date

    private LocalDate dataTermino; //Date

    private BigDecimal orcamento; //Numeric(15,2) no banco

    private String descricao; //VARCHAR

    private Long gerenteId; //VARCHAR

    private Status status;

    private ClassificacaoRisco risco;

    @ElementCollection
    @CollectionTable(
            name = "projeto_membros",
            joinColumns = @JoinColumn(name = "projeto_id")
    )
    @Column(name = "membro_id")
    private List<Long> membrosIds = new ArrayList<>();

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setPrevisaoTermino(LocalDate previsaoTermino) {
        this.previsaoTermino = previsaoTermino;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setOrcamento(BigDecimal orcamento) {
        this.orcamento = orcamento;
    }

    public void setDataTermino(LocalDate dataTermino) {
        this.dataTermino = dataTermino;
    }

    public void setGerente(Long gerenteId) {
        this.gerenteId = gerenteId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setRisco(ClassificacaoRisco risco) {
        this.risco = risco;
    }

    public void setMembrosIds(List<Long> membrosIds) {
        this.membrosIds = membrosIds;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getPrevisaoTermino() {
        return previsaoTermino;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public Status getStatus() {
        return status;
    }

    public Long getGerenteID() {
        return gerenteId;
    }

    public ClassificacaoRisco getRisco() {
        return risco;
    }

    public List<Long> getMembrosIds() {
        return membrosIds;
    }
}

