package com.example.fatecCarCarona.dto;

import java.time.LocalDateTime;

public record RideResponseDTO(
		Long id,
		VehicleResponseDTO vehicle,
		OriginResponseDTO origin,
		DestinationResponseDTO destination,
		Integer vagas_disponiveis,
		LocalDateTime data_hora,
		String status

) {

}
