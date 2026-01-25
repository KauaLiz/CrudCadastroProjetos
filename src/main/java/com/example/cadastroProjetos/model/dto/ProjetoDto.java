package com.example.cadastroProjetos.model.dto;

import com.example.cadastroProjetos.model.enums.ClassificacaoRisco;
import com.example.cadastroProjetos.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjetoDto(

        @NotNull
        String nome,

        @NotNull
        @Schema(type = "string", pattern = "dd/MM/yyyy", example = "25/12/2025")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataInicio,

        @NotNull
        @Schema(type = "string", pattern = "dd/MM/yyyy", example = "25/12/2025")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate previsaoTermino,

        @Schema(type = "string", pattern = "dd/MM/yyyy", example = "25/12/2025")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataTermino,

        @NotNull
        @Min(0)
        BigDecimal orcamento,

        String descricao,
        Long gerenteId,
        List<Long> membrosIds
) {}

