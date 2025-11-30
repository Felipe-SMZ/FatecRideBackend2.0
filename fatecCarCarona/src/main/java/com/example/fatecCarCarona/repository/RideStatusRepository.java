package com.example.fatecCarCarona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatecCarCarona.entity.RideStatus;

public interface RideStatusRepository extends JpaRepository<RideStatus, Long> {

	RideStatus findByNome(String nome);

}
