package com.example.cadastroProjetos.model.dto;

import jakarta.validation.constraints.NotNull;

public record MembroDto(

        @NotNull
        String nome,
        @NotNull
        String cargo
) {
}
