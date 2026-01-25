package com.example.cadastroProjetos.model.dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticationDto(
        @NotNull
        String login,
        @NotNull
        String senha
) {
}
