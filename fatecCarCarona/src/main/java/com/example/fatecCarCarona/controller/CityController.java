package com.example.fatecCarCarona.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.CityDTO;
import com.example.fatecCarCarona.service.CityService;

@RestController
@RequestMapping("/cities")
public class CityController {
	@Autowired
	CityService cityService;

	@GetMapping("/{id_estado}")
	public ResponseEntity<List<CityDTO>> getCitiesByStateId(@PathVariable("id_estado") Long id_estado) throws Exception {
		List<CityDTO> cities = cityService.allCitiessByStateId(id_estado);
		return ResponseEntity.ok(cities);
	}


}
