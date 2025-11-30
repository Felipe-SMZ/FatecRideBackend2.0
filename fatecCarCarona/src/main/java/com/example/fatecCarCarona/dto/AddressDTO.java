package com.example.fatecCarCarona.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) 
public record AddressDTO(
		String road,  
		String suburb,  
		String city,  
		String county,  
		String state,  
		String postcode, 
		String country		
) {}
