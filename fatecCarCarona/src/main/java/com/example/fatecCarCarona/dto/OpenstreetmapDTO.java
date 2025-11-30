package com.example.fatecCarCarona.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenstreetmapDTO(
		Long place_id,
	    String licence,
	    String osm_type,
	    Long osm_id,
	    String lat,
	    String lon,
	    @JsonProperty("class") String clazz,
	    String type,
	    Integer place_rank,
	    Double importance,
	    String addresstype,
	    String name,
	    String display_name,
	    List<String> boundingbox,
	    AddressDTO address 
	    ) {}
