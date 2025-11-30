package com.example.fatecCarCarona.dto;

import java.time.LocalDateTime;

public record CompletedPassengerRequestDTO(
		Long id,
		OriginResponseDTO originDTO,
		DestinationResponseDTO destinationDTO,
		Long id_carona,
		LocalDateTime dataHora,
		String status, // ✅ ADICIONAR
		Long id_status_solicitacao,       // ✅ ADICIONAR
		
		// ✅ ADICIONAR: Dados do motorista
	    String nome_motorista,
	    String foto_motorista,
	    String curso_motorista,
	    
	    // ✅ ADICIONAR: Dados do veículo
	    String veiculo_modelo,
	    String veiculo_marca,
	    String veiculo_placa,
	    String veiculo_cor

) {}
