package com.example.fatecCarCarona.dto;

public record PassageRequestsDTO(
		OriginDTO originDTO,
		DestinationDTO destinationDTO,
		Long id_carona
) {}
