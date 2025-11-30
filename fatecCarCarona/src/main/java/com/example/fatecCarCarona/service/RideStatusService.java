package com.example.fatecCarCarona.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.entity.RideStatus;
import com.example.fatecCarCarona.repository.RideStatusRepository;

@Service
public class RideStatusService {

	@Autowired
	RideStatusRepository rideStatusRepository;

	public RideStatus gellByName(String nome) {
		return rideStatusRepository.findByNome(nome);
	}

}
