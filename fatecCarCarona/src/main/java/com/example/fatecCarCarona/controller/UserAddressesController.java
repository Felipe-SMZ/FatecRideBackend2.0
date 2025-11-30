package com.example.fatecCarCarona.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.UserAddressesDTO;
import com.example.fatecCarCarona.dto.UserAddressesResponseDTO;
import com.example.fatecCarCarona.repository.UserRepository;
import com.example.fatecCarCarona.service.TokenService;
import com.example.fatecCarCarona.service.UserAddressesService;

@RestController
@RequestMapping("/address")
public class UserAddressesController {
	@Autowired
	UserAddressesService userAddressesService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private  TokenService tokenService;

	@GetMapping
	public ResponseEntity<UserAddressesResponseDTO> getMyAddresses(@RequestHeader("Authorization") String authHeader){
		Long idLong = tokenService.extractUserIdFromHeader(authHeader);
		UserAddressesResponseDTO addressesResponseDTO = userAddressesService.getMyAddresses(idLong);
		return ResponseEntity.ok(addressesResponseDTO);


	}

	@PutMapping("/{idAddres}")
	public ResponseEntity<UserAddressesDTO> updateMyAddresses(
			@RequestHeader("Authorization") String authHeader,
			@PathVariable Long idAddres,
			@RequestBody UserAddressesDTO userAddressesDTO ) throws Exception{
		Long idLong = tokenService.extractUserIdFromHeader(authHeader);
		UserAddressesDTO addressesResponseDTO = userAddressesService.updateMyAddresses(idLong,idAddres, userAddressesDTO);
		return ResponseEntity.ok(addressesResponseDTO);


	}


}
