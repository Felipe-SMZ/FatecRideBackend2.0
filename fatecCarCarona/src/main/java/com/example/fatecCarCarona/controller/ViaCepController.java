package com.example.fatecCarCarona.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.ViaCepDTO;
import com.example.fatecCarCarona.service.ViaCepService;

@RestController
@RequestMapping("/cep")
public class ViaCepController {
	   @Autowired
	    private ViaCepService viaCepService;

	    @GetMapping("/{cep}")
	    public Optional<ViaCepDTO> buscarEndereco(@PathVariable String cep) {
	        return viaCepService.buscarCep(cep);
	    }
}
