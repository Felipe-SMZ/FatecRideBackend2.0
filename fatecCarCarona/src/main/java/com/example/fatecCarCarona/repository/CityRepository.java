package com.example.fatecCarCarona.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.fatecCarCarona.entity.City;

public interface CityRepository extends JpaRepository<City, Long>{

	@Query("SELECT c FROM City c WHERE c.estado.id = ?1")
	List<City> findByStateId(Long stateId);

	@Query("SELECT c FROM City c WHERE c.nome = ?1")
	Optional<City> findByNome(String nome);

}
