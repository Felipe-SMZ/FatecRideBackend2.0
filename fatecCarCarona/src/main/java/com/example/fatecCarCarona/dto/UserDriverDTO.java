package com.example.fatecCarCarona.dto;

public record UserDriverDTO(
		String nome,
	    String sobrenome,
	    String email,
	    String senha,
	    String telefone,
	    String foto,
	    Long userTypeId,
	    Long genderId,
	    Long courseId,
	    UserAddressesDTO userAddressesDTO,
	    VehicleDTO vehicleDTO
) {

}
