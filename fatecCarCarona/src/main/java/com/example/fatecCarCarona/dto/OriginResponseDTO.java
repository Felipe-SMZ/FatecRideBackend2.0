package com.example.fatecCarCarona.dto;

public record OriginResponseDTO(
		Long id,
		String cidade,
	    String logradouro,
	    String numero,
	    String bairro,
	    String cep
){}
