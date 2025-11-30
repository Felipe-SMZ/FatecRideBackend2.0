package com.example.fatecCarCarona.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.StateDTO;
import com.example.fatecCarCarona.entity.State;
import com.example.fatecCarCarona.service.StateService;

@RestController
@RequestMapping("/states")
public class StateController {
	@Autowired
	StateService stateService;

	@GetMapping
	public ResponseEntity<List<StateDTO>> getAll(){
		List<StateDTO> getAll = stateService.getAll();
		return ResponseEntity.ok(getAll);
	}

	@GetMapping("/{id}")
	public ResponseEntity<State> get(@PathVariable("id") Long id) throws Exception{
		Optional<State> optionalState = stateService.get(id);
		return optionalState.map(ResponseEntity::ok)
		                    .orElseGet(() -> ResponseEntity.notFound().build());
	}


}

