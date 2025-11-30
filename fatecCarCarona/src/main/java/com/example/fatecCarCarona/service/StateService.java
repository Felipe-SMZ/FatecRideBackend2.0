package com.example.fatecCarCarona.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.dto.StateDTO;
import com.example.fatecCarCarona.entity.State;
import com.example.fatecCarCarona.repository.StateRepository;
@Service
public class StateService {

	@Autowired
	StateRepository stateRepository;




	public List<StateDTO> getAll() {
		  try {
			  List<State> allStates = stateRepository.findAll();
			return 	allStates.stream()
					.map(state ->new  StateDTO(state.getId(),state.getNome(), state.getUf(), state.getIbge(), state.getPais(), state.getDdd()))
					.collect(Collectors.toList());
		  } catch (Exception e) {
			throw new RuntimeException("Erro ao buscar estados de", e);
		  }
	}



	public Optional<State> get(Long id) throws Exception {
			return validateStateExists(id);
	}




	public Optional<State>  validateStateExists(Long stateId) throws Exception{
		Optional<State> stateExists = stateRepository.findById(stateId);
		if(stateExists.isEmpty()) {
			throw new Exception("Estado inexistente");
		}
		return stateExists;

	}

}
