package com.example.cadastroProjetos.model.dto;

import com.example.cadastroProjetos.model.enums.Status;
import java.math.BigDecimal;
import java.util.Map;

public record RelatorioDto(
        Map<Status, Long> quantidadePorStatus,
        Map<Status, BigDecimal> totalOrcadoPorStatus,
        Long mediaDuracaoProjetosEncerrados,
        Long totalMembrosUnicos
) {
}
