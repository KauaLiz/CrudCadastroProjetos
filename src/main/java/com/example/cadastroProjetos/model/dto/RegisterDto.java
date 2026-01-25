package com.example.cadastroProjetos.model.dto;

import com.example.cadastroProjetos.model.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record RegisterDto(
        @NotNull
        String login,
        @NotNull
        String senha,
        @NotNull
        UserRole role
) {
}
