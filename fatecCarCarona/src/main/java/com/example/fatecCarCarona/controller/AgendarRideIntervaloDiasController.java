package com.example.fatecCarCarona.controller;

import com.example.fatecCarCarona.dto.AgendarRideIntervaloDiasDTO;
import com.example.fatecCarCarona.dto.AgendarRideIntervaloDiasResponseDTO;
import com.example.fatecCarCarona.service.AgendarRideIntervaloDiasService;
import com.example.fatecCarCarona.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendar-compromisso-intervalo-dias")
public class AgendarRideIntervaloDiasController {

    @Autowired
    AgendarRideIntervaloDiasService agendarRideIntervaloDiasService;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<AgendarRideIntervaloDiasResponseDTO> agendarRide(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid AgendarRideIntervaloDiasDTO agendarCompromisso) {

        Long idLong = tokenService.extractUserIdFromHeader(authHeader);
        AgendarRideIntervaloDiasResponseDTO result =
                agendarRideIntervaloDiasService.criarNaAgendaRide(idLong, agendarCompromisso);
        return ResponseEntity.status(HttpStatus.CREATED).body(result); // ✅ 201 em criação
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<Void> desativar(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        Long idLong = tokenService.extractUserIdFromHeader(authHeader);
        agendarRideIntervaloDiasService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ CORRIGIDO: tipo do retorno alinhado com o service
    @GetMapping
    public ResponseEntity<List<AgendarRideIntervaloDiasResponseDTO>> pegarTodos(
            @RequestHeader("Authorization") String authHeader) {

        Long idLong = tokenService.extractUserIdFromHeader(authHeader);
        List<AgendarRideIntervaloDiasResponseDTO> todos =
                agendarRideIntervaloDiasService.pegarTodos(idLong);
        return ResponseEntity.ok(todos);
    }
}