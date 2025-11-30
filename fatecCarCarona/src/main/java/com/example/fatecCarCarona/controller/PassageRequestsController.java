package com.example.fatecCarCarona.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.example.fatecCarCarona.dto.CompletedPassengerRequestDTO;
import com.example.fatecCarCarona.dto.NearbyDriversDTO;
import com.example.fatecCarCarona.dto.PassageRequestsDTO;
import com.example.fatecCarCarona.dto.PassengerSearchRequest;
import com.example.fatecCarCarona.dto.PendingPassengerRequestDTO;
import com.example.fatecCarCarona.dto.RideResponseDTO;
import com.example.fatecCarCarona.service.PassageRequestsService;
import com.example.fatecCarCarona.service.TokenService;



@RestController
@RequestMapping("/solicitacao")
public class PassageRequestsController {
	@Autowired
	PassageRequestsService passageRequestsService;
	@Autowired
	private  TokenService tokenService;
	@PostMapping("/proximos")
	public ResponseEntity<List<NearbyDriversDTO>> findNearbyDrivers(@RequestBody PassengerSearchRequest passengerSearchRequest) throws Exception{
		List<NearbyDriversDTO> motoristaProximos = passageRequestsService.findNearbyDrivers(passengerSearchRequest);
		
		return new ResponseEntity<List<NearbyDriversDTO>>(motoristaProximos,HttpStatus.OK);
	
	}
	
	@PostMapping
	public ResponseEntity<PassageRequestsDTO> create(@RequestHeader("Authorization") String authHeader,@RequestBody PassageRequestsDTO passageRequests) throws Exception {
		Long idLong = tokenService.extractUserIdFromHeader(authHeader);
		PassageRequestsDTO passageRequestsDTO = passageRequestsService.create(passageRequests, idLong);
		return ResponseEntity.ok(passageRequestsDTO);
	}
	
	
	@PutMapping("cancelar/{id_solicitacao}")
    public ResponseEntity<String> cancel(@RequestHeader("Authorization") String authHeader, @PathVariable Long id_solicitacao) {

    		Long idLong = tokenService.extractUserIdFromHeader(authHeader);

            passageRequestsService.cancelar(idLong, id_solicitacao);
            return ResponseEntity.ok("Solicitação cancelada com sucesso.");

    }
	
	 @GetMapping("/concluidas") 
	 public ResponseEntity<?> listarSolicitacoesConcluidas(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int itens) { 
		 try { 
		  	Long userId = tokenService.extractUserIdFromHeader(authorizationHeader); 
	   		Page<CompletedPassengerRequestDTO> solicitacoesConcluidas = passageRequestsService .buscarSolicitacoesConcluidas(userId, pagina, itens); 
	 
	   	 if (solicitacoesConcluidas.isEmpty()) { 
		   return ResponseEntity.noContent().build(); 
	   	 } 
	 
	   	 	return ResponseEntity.ok(solicitacoesConcluidas); 
		 } catch (Exception e) { 
			 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou usuário não autorizado."); 
		 } 
	 } 
	 
	 @GetMapping("/pending")
	    public ResponseEntity<List<PendingPassengerRequestDTO>> getPendingRequests(@RequestHeader("Authorization") String authorizationHeader) {
	        try {
	            Long userId = tokenService.extractUserIdFromHeader(authorizationHeader);
	            List<PendingPassengerRequestDTO> dtoList = passageRequestsService.getPendingRequests(userId);
	            return ResponseEntity.ok(dtoList);
	        } catch (IllegalArgumentException e) {
	            // usuário não encontrado ou token inválido/ausente
	            return ResponseEntity.status(401).build();
	        } catch (Exception e) {
	            // erro inesperado
	            return ResponseEntity.status(500).build();
	        }
	    }
}
