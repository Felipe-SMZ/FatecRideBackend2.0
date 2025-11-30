package com.example.fatecCarCarona.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.entity.PassageRequestsStatus;
import com.example.fatecCarCarona.entity.PassageRequestsStatusRepository;

@Service
public class PassageRequestsStatusService {

	@Autowired
	PassageRequestsStatusRepository passageRequestsStatusRepository;

	public PassageRequestsStatus findByNome(String nome) {
        return passageRequestsStatusRepository.findByNome(nome);
    }
}
