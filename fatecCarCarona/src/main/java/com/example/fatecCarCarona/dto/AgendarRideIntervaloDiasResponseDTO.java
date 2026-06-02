package com.example.fatecCarCarona.dto;

import java.time.LocalDate;

public record AgendarRideIntervaloDiasResponseDTO(
        Long id,
        Long rideId,
        String origemLogradouro,
        String origemBairro,
        String destinoLogradouro,
        String destinoBairro,
        Long driverId,
        String driverNome,
        String driverSobrenome,
        LocalDate dataInicio,
        Long intervaloDiasId,
        Integer intervaloDiasQuantidadeDias,
        boolean ativo
) {
}