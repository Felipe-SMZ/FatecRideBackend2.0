package com.example.fatecCarCarona.dto;

public record LoginReposnseDTO(
		String name ,
		String token,
	    Long id,              // ID do usuário
	    Long userTypeId       // Tipo de usuário (1=MOTORISTA, 2=PASSAGEIRO, 3=AMBOS)
		) {

}
