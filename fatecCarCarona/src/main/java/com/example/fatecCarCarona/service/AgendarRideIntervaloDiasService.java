package com.example.fatecCarCarona.service;

import com.example.fatecCarCarona.dto.AgendarRideIntervaloDiasDTO;
import com.example.fatecCarCarona.dto.AgendarRideIntervaloDiasResponseDTO;
import com.example.fatecCarCarona.entity.AgendarRideIntervaloDias;
import com.example.fatecCarCarona.entity.IntervaloDias;
import com.example.fatecCarCarona.entity.Ride;
import com.example.fatecCarCarona.entity.User;
import com.example.fatecCarCarona.repository.AgendarRideIntervaloDiasRepository;
import com.example.fatecCarCarona.repository.IntervaloDiasRepository;
import com.example.fatecCarCarona.repository.RideRepository;
import com.example.fatecCarCarona.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AgendarRideIntervaloDiasService {

    @Autowired
    RideRepository rideRepository;

    @Autowired
    IntervaloDiasRepository intervaloDiasRepository;

    @Autowired
    AgendarRideIntervaloDiasRepository agendarRideIntervaloDiasRepository;

    @Autowired
    UserRepository userRepository;

    public AgendarRideIntervaloDiasResponseDTO criarNaAgendaRide(Long idLong, AgendarRideIntervaloDiasDTO agendarRide) {

        User user = userRepository.findById(idLong)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario não encontrado"));

        // ✅ CORRIGIDO: era RuntimeException genérica → HTTP 500
        Ride ride = rideRepository.findById(agendarRide.ride())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ride não encontrado"));

        if (!ride.getDriver().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta carona não pertence a este motorista.");
        }

        IntervaloDias intervaloDias = intervaloDiasRepository.findById(agendarRide.intervalo_dias())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Intervalo de dia não encontrado"));

        AgendarRideIntervaloDias insert = new AgendarRideIntervaloDias();
        insert.setRide(ride);
        insert.setDia_agendamento(intervaloDias);
        // ✅ CORRIGIDO: usava LocalDate.now() ignorando o campo do DTO
        insert.setDataInicio(agendarRide.dataInicio());
        insert.setAtivo(true);

        // ✅ CORRIGIDO: variável 'criarNaAgenda' era criada e ignorada
        AgendarRideIntervaloDias salvo = agendarRideIntervaloDiasRepository.save(insert);

        return toResponseDTO(salvo);
    }

    public void desativar(Long id) {

        AgendarRideIntervaloDias agenda = agendarRideIntervaloDiasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

        if (!agenda.isAtivo()) {
            return;
        }

        agenda.setAtivo(false);
        agendarRideIntervaloDiasRepository.save(agenda);
    }

    // ✅ CORRIGIDO: retornava entidade JPA diretamente na API
    public List<AgendarRideIntervaloDiasResponseDTO> pegarTodos(Long idLong) {
        User user = userRepository.findById(idLong)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario não encontrado"));

        List<AgendarRideIntervaloDias> lista =
                agendarRideIntervaloDiasRepository.findByRideDriverIdAndAtivoTrue(user.getId());

        return lista.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // ✅ NOVO: metodo de mapeamento centralizado
    private AgendarRideIntervaloDiasResponseDTO toResponseDTO(AgendarRideIntervaloDias entity) {
        return new AgendarRideIntervaloDiasResponseDTO(
                entity.getId(),
                entity.getRide().getId(),
                entity.getRide().getOrigin().getLogradouro(),
                entity.getRide().getOrigin().getBairro(),
                entity.getRide().getDestination().getLogradouro(),
                entity.getRide().getDestination().getBairro(),
                entity.getRide().getDriver().getId(),
                entity.getRide().getDriver().getNome(),
                entity.getRide().getDriver().getSobrenome(),
                entity.getDataInicio(),
                entity.getDia_agendamento().getId(),
                entity.getDia_agendamento().getQuantidade_dias(),
                entity.isAtivo()
        );
    }
}
