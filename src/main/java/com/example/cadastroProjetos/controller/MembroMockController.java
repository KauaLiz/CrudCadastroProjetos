package com.example.cadastroProjetos.controller;

import com.example.cadastroProjetos.model.dto.MembroDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@Hidden
@RequestMapping("/membro")
public class MembroMockController {

    private final HashMap<Long, MembroDto> bancoMembro = new HashMap<>();
    private Long id = 1L;

    @PostMapping("/criar")
    public ResponseEntity<String> criarMembro(@RequestBody @Valid MembroDto data) {
        MembroDto membroEntity = data;
        bancoMembro.put(id++, membroEntity);

        bancoMembro.put(1L, new MembroDto("Kauã", "Gerente"));
        bancoMembro.put(2L, new MembroDto("Pedro", "Funcionário"));
        bancoMembro.put(3L, new MembroDto("Augusto", "Funcionário"));
        bancoMembro.put(4L, new MembroDto("Ana", "Funcionário"));
        bancoMembro.put(5L, new MembroDto("Fulano", "Funcionário"));
        bancoMembro.put(6L, new MembroDto("Felipe", "Funcionário"));
        bancoMembro.put(7L, new MembroDto("Augusta", "Funcionário"));
        bancoMembro.put(8L, new MembroDto("Caring", "Funcionário"));
        bancoMembro.put(9L, new MembroDto("Kadson", "Funcionário"));
        bancoMembro.put(10L, new MembroDto("Antonio", "Funcionário"));
        bancoMembro.put(11L, new MembroDto("Jorge", "Funcionário"));
        bancoMembro.put(12L, new MembroDto("Otavio", "Funcionário"));
        return ResponseEntity.ok("Membro Criado com sucesso");
    }

    @GetMapping("/retornarMembro/{id}")
    public MembroDto consultarMembro(@PathVariable Long id) {
        return bancoMembro.get(id);
    }

    @GetMapping("/retornarMembros")
    public ResponseEntity<HashMap<Long, MembroDto>> consultarMembro() {
        return ResponseEntity.ok(bancoMembro);
    }

}

