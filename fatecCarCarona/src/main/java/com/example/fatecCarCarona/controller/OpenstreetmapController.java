package com.example.fatecCarCarona.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.OpenstreetmapDTO;
import com.example.fatecCarCarona.service.OpenstreetmapService;

@RestController
@RequestMapping
public class OpenstreetmapController {

	@Autowired
	OpenstreetmapService openstreetmapService;

	@GetMapping("/local")
	public ResponseEntity<?> buscarLocal(@RequestParam String local) {

	     try { 
	         Optional<OpenstreetmapDTO> resultado = 
	openstreetmapService.buscarLocal(local); 
	         if (resultado.isPresent()) { 
	             return ResponseEntity.ok(resultado.get()); 
	         } else { 
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Local não encontrado"); 
	         } 
	     } catch (Exception e) { 
	         e.printStackTrace(); 
	         return 
	ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar localização"); 
	     } 

	}
}
