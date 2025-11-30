package com.example.fatecCarCarona.dto;

public record RideDTO(
		OriginDTO originDTO,
		DestinationDTO destinationDTO,
		Integer vagas_disponiveis,
		Long id_veiculo
) {



}
