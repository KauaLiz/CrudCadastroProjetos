package com.example.cadastroProjetos.model.dto;

import java.util.List;

public record ProjetoRequestDto(
        List<Long> membrosIds
) { }
