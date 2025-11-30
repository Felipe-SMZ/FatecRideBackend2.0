package com.example.fatecCarCarona.dto;

public record UserAddressesResponseDTO(
		Long id,
		String city,
	    String logradouro,
	    String numero,
	    String bairro,
	    String cep
) {

}
