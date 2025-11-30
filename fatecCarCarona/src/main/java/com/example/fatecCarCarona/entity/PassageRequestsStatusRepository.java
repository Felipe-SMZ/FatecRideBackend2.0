package com.example.fatecCarCarona.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PassageRequestsStatusRepository extends JpaRepository<PassageRequestsStatus, Long>{

	@Query("SELECT s FROM PassageRequestsStatus s where s.nome= ?1")
	PassageRequestsStatus findByNome(String nome);

	
}
