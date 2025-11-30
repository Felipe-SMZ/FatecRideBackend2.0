package com.example.fatecCarCarona.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PendingPassengerRequestDTO(
    @JsonProperty("id_solicitacao") Long id_solicitacao,
    @JsonProperty("id_motorista") Long id_motorista,
    @JsonProperty("nome_motorista") String nome_motorista,
    @JsonProperty("foto") String foto,
    @JsonProperty("curso_motorista") String curso_motorista,
    @JsonProperty("originDTO") OriginDTO originDTO,
    @JsonProperty("destinationDTO") DestinationDTO destinationDTO,
    @JsonProperty("id_carona") Long id_carona,
    @JsonProperty("status") String status,
    @JsonProperty("id_status_solicitacao") Long id_status_solicitacao
) {}