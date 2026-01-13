package com.example.cadastroProjetos.model.enums;

public enum ClassificacaoRisco {
    BAIXO("Baixo"),
    MEDIO("Médio"),
    ALTO("Alto");

    private String descricao;

    ClassificacaoRisco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static ClassificacaoRisco converterEnum(String descricao) {
        for (ClassificacaoRisco risco : values()) {
            if (risco.getDescricao().equals(descricao)) {
                return risco;
            }
        }
        throw new IllegalArgumentException("Classificação Inválida: " + descricao);
    }
}
