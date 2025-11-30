package com.example.fatecCarCarona.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fatecCarCarona.entity.User;
import com.example.fatecCarCarona.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>{
	List<Vehicle> findAllByUser(User user);

	Optional<Vehicle> findByPlaca(String placa);


}
