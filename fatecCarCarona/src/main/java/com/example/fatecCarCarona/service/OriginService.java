package com.example.fatecCarCarona.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.entity.Origin;
import com.example.fatecCarCarona.repository.OriginRepository;

@Service
public class OriginService {

	@Autowired
	OriginRepository originRepository;

	public Origin createOrigin(Origin origin) {
		return originRepository.save(origin);
	}

	public Origin findById(Long id) {
		// TODO Auto-generated method stub
		return originRepository.findById(id).orElseThrow(() -> new RuntimeException("id origem não encontrada"));
	}

}
