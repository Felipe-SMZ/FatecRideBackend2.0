package com.example.fatecCarCarona.controller;


import com.example.fatecCarCarona.dto.AgendarRideDiaSemanaDTO;
import com.example.fatecCarCarona.dto.ListaDiaSemanas;
import com.example.fatecCarCarona.service.AgendarRideDiaSemanaService;
import com.example.fatecCarCarona.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/agendar-ride-dia-semana")
public class AgendarRideDiaSemanaController {
    @Autowired
    AgendarRideDiaSemanaService agendarRideDiaSemanaService;


    @Autowired
    private TokenService tokenService;


    @PostMapping
    public ResponseEntity<AgendarRideDiaSemanaDTO> criar(@RequestHeader("Authorization") String authHeader, @RequestBody AgendarRideDiaSemanaDTO agendarRide) {
        Long idLong = tokenService.extractUserIdFromHeader(authHeader);
        AgendarRideDiaSemanaDTO results = agendarRideDiaSemanaService.criarNaAgendaRide(idLong, agendarRide);
        return ResponseEntity.ok(results);
    }


    @PutMapping("/desativar")
    public ResponseEntity<Void> desativar(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ListaDiaSemanas diasSemana) {

        Long idLong = tokenService.extractUserIdFromHeader(authHeader);
        agendarRideDiaSemanaService.desativar(idLong, diasSemana);
        return ResponseEntity.noContent().build();
    }


    //fazer
    @GetMapping
    public ResponseEntity<List<AgendarRideDiaSemanaDTO>> pegarTodos(@RequestHeader("Authorization") String authHeader) {
        Long idLong = tokenService.extractUserIdFromHeader(authHeader);

        List<AgendarRideDiaSemanaDTO> todos = agendarRideDiaSemanaService.pegarTodos(idLong);
        return ResponseEntity.ok(todos);
    }


}
