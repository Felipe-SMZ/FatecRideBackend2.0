package com.example.fatecCarCarona.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.GenderDTO;
import com.example.fatecCarCarona.service.GenderService;

@RestController
@RequestMapping("/genders")
public class GenderController {

	@Autowired
	GenderService genderService;

	@GetMapping
	public ResponseEntity<List<GenderDTO>> allGender(){
		List<GenderDTO> genderDTO = genderService.allGender();
		return ResponseEntity.ok(genderDTO);
	}
}
