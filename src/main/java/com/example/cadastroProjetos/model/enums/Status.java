package com.example.cadastroProjetos.model.enums;

public enum Status {
    EM_ANALISE("Em análise"),
    ANALISE_REALIZADA("Análise Realizada"),
    ANALISE_APROVADA("Análise Aprovada"),
    INICIADO("Iniciado"),
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    ENCERRADO("Encerrado"),
    CANCELADO("Cancelado");

    private final String descricao;

    Status(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao(){
        return descricao;
    }

    public static Status converterEnum(String descricaoBanco){
        for(Status status : values()) {
            if (status.descricao.equals(descricaoBanco)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Status Invalido: " + descricaoBanco);
    }
}


