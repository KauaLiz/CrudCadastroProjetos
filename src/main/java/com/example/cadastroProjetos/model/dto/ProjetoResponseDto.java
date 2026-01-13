package com.example.cadastroProjetos.model.dto;

import com.example.cadastroProjetos.model.enums.ClassificacaoRisco;
import com.example.cadastroProjetos.model.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjetoResponseDto(
        String nome,
        LocalDate dataInicio,
        LocalDate previsaoTermino,
        LocalDate dataTermino,
        BigDecimal orcamento,
        String descricao,
        Long gerenteId,
        List<Long> membrosIds,
        Status status,
        ClassificacaoRisco risco
) {}
