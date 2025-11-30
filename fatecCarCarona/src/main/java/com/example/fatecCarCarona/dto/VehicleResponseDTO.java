package com.example.fatecCarCarona.dto;

public record VehicleResponseDTO(
		 Long id,
	     String modelo,
	     String marca,
	     String placa,
	     String cor,
	     Integer ano,
	     Integer vagas_disponiveis
)
{}
