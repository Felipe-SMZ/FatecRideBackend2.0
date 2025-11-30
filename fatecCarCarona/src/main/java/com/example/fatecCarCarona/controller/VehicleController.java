package com.example.fatecCarCarona.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.VehicleDTO;
import com.example.fatecCarCarona.dto.VehicleResponseDTO;
import com.example.fatecCarCarona.service.TokenService;
import com.example.fatecCarCarona.service.VehicleService;

@RestController
@RequestMapping("/veiculos")
public class VehicleController {
	@Autowired
	VehicleService vehicleService;
	@Autowired
	TokenService tokenService;

	@GetMapping
	public ResponseEntity<List<VehicleResponseDTO>> getAllCarsByDriver(@RequestHeader("Authorization") String authHeader){
		Long idLong = tokenService.extractUserIdFromHeader(authHeader);

	    List<VehicleResponseDTO> vehicles = vehicleService.getAllCarsByDriver(idLong);
		return  ResponseEntity.ok(vehicles);
	}


	@GetMapping("/{id_veiculo}")
	public ResponseEntity<VehicleDTO> getCarByDriver(@RequestHeader("Authorization") String authHeader, @PathVariable(name = "id_veiculo") Long id_veiculo ) throws Exception{
		Long idLong = tokenService.extractUserIdFromHeader(authHeader);

	    VehicleDTO vehicle = vehicleService.getCarByDriver(idLong,id_veiculo);
		return  ResponseEntity.ok(vehicle);
	}

	@PutMapping("/{id_veiculo}")
	public ResponseEntity<VehicleDTO> puCarByDriver(@RequestHeader("Authorization") String authHeader, @PathVariable(name = "id_veiculo") Long id_veiculo, @RequestBody VehicleDTO vehicleDTO ) throws Exception{
		Long idLong = tokenService.extractUserIdFromHeader(authHeader);

	    VehicleDTO vehicle = vehicleService.putCarByDriver(idLong,id_veiculo,vehicleDTO);
		return  ResponseEntity.ok(vehicle);
	}

	@PostMapping
	public ResponseEntity<VehicleDTO> postCarByDriver(@RequestHeader("Authorization") String authHeader, @RequestBody VehicleDTO vehicleDTO ) throws Exception{
		Long idLong = tokenService.extractUserIdFromHeader(authHeader);
	    VehicleDTO vehicle = vehicleService.postCarByDriver(idLong, vehicleDTO);
		return  ResponseEntity.ok(vehicle);
	}

		@DeleteMapping("/{id_veiculo}")
		public ResponseEntity<?> puCarByDriver(@RequestHeader("Authorization") String authHeader, @PathVariable(name = "id_veiculo") Long id_veiculo) throws Exception{
			Long idLong = tokenService.extractUserIdFromHeader(authHeader);

		    vehicleService.deleteCarByDriver(idLong,id_veiculo);
			return  ResponseEntity.ok("carro deletado");
		}
}
