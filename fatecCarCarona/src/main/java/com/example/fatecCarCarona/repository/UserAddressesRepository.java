package com.example.fatecCarCarona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fatecCarCarona.entity.UserAddresses;

public interface UserAddressesRepository extends JpaRepository<UserAddresses, Long>{
	@Query("SELECT a FROM  UserAddresses a where a.user.id = :userId")
	UserAddresses getByUser(@Param("userId") Long userId);

}
