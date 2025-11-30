package com.example.fatecCarCarona.dto;

public record RequestsForMyRideDTO(
		Long id_solicitacao,
		Long id_passageiro,         // novo campo
		String nome_passageiro,
		String foto,
		String curso,
		OriginDTO originDTO,
		DestinationDTO destinationDTO,
		Long id_carona,
		String status,
		Long id_status_solicitacao 
) {
}
