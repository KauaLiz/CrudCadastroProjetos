package com.example.cadastroProjetos.service;

import com.example.cadastroProjetos.model.dto.MembroDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MembroApiMockada {

    public MembroDto consultarID(Long id){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MembroDto> response =
                restTemplate.
                        getForEntity("http://localhost:8080/membro/retornarMembro/" + id, MembroDto.class);

        return response.getBody();
    }
}
