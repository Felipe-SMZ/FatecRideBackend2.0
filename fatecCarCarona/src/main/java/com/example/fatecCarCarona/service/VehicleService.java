package com.example.fatecCarCarona.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.dto.VehicleDTO;
import com.example.fatecCarCarona.dto.VehicleResponseDTO;
import com.example.fatecCarCarona.entity.User;
import com.example.fatecCarCarona.entity.Vehicle;
import com.example.fatecCarCarona.repository.UserRepository;
import com.example.fatecCarCarona.repository.VehicleRepository;

@Service
public class VehicleService {
	@Autowired
	VehicleRepository vehicleRepository;

	@Autowired
	TokenService tokenService;

	@Autowired
	UserRepository userRepository;


	public Boolean validatePlacaExists(String placa,Long id_usuario) throws Exception {

            Optional<Vehicle> existingVehicleByPlaca = vehicleRepository.findByPlaca(placa);

            if (existingVehicleByPlaca.isPresent() && !existingVehicleByPlaca.get().getUser().getId().equals(id_usuario)) {
            	throw new Exception("Placa já cadastrada por outro usuário");
            }

            return true;

	}

	public Vehicle convertDtoToVehicle(VehicleDTO vehicleDTO, User user) {
		Vehicle  vehicle = new  Vehicle ();
		vehicle.setUser(user);
		vehicle.setModelo(vehicleDTO.modelo());
		vehicle.setMarca(vehicleDTO.marca());
		vehicle.setPlaca(vehicleDTO.placa());
		vehicle.setCor(vehicleDTO.cor());
		vehicle.setAno(vehicleDTO.ano());
		vehicle.setAvailableSeats(vehicleDTO.vagas_disponiveis());

		return vehicle;
	}

		public VehicleDTO convertVehicleToDTO(Vehicle vehicle) {
			 return new VehicleDTO(
					 vehicle.getModelo(),
					 vehicle.getMarca(),
					 vehicle.getPlaca(),
					 vehicle.getCor(),
					 vehicle.getAno(),
					 vehicle.getAvailableSeats()
		         );
		}
		public VehicleResponseDTO convertVehicleToReponseDTO(Vehicle vehicle) {

			 return new VehicleResponseDTO(
					 vehicle.getId(),
					 vehicle.getModelo(),
					 vehicle.getMarca(),
					 vehicle.getPlaca(),
					 vehicle.getCor(),
					 vehicle.getAno(),
					 vehicle.getAvailableSeats()
		         );
		}



		public boolean validateUserIsVehicleOwner(Long idLong, Long id_veiculo) throws Exception {
			Vehicle vehicle = vehicleRepository.findById(id_veiculo).orElseThrow(() -> new Exception("Veiculo não encntrado"));

			if(!vehicle.getUser().getId().equals(idLong)) {
				throw new Exception("Não permitido pois esse veiculo não opertence a esse usuario");
			}
			return true;
		}

	public Vehicle createVehicle(Vehicle vehicle) {
		return  vehicleRepository.save(vehicle);
	}


	public VehicleDTO cadastrarVehehicle( VehicleDTO vehicleDTO, User user) {
		if(vehicleDTO.vagas_disponiveis() <= 0) {
			throw new RuntimeException("Vagas não podem ser iguais ou menor a 0");

		}
		Vehicle convertDtoToVehicle = convertDtoToVehicle(vehicleDTO , user);
		Vehicle save =  createVehicle(convertDtoToVehicle);
		return convertVehicleToDTO(save);

	}

	public List<VehicleResponseDTO> getAllCarsByDriver(Long idLong) {

		User user = userRepository.findById(idLong).orElseThrow(() -> new RuntimeException("usuario não encontrado"));
		List<Vehicle> vehicles = vehicleRepository.findAllByUser(user);

		if (vehicles.isEmpty()) {
		        throw new RuntimeException("Nenhum veículo encontrado para este motorista.");
		    }
		List<VehicleResponseDTO> vehiclesDTO = new ArrayList<>();

		 for(Vehicle v : vehicles) {
			 vehiclesDTO.add(convertVehicleToReponseDTO(v));
		 }

		return vehiclesDTO;
	}

	public VehicleDTO getCarByDriver(Long idLong, Long id_veiculo) throws Exception {
		Vehicle vehicle = vehicleRepository.findById(id_veiculo).orElseThrow(() -> new Exception("Veiculo não encntrado"));
		if(!vehicle.getUser().getId().equals(idLong)) {
			throw new Exception("Não permitido pois esse veiculo não opertence a esse usuario");
		}
		VehicleDTO vehicleDTO = convertVehicleToDTO(vehicle);
		return vehicleDTO;
	}

	public VehicleDTO putCarByDriver(Long idLong, Long id_veiculo,VehicleDTO vehicleDTO) throws Exception {
		Vehicle vehicle = vehicleRepository.findById(id_veiculo).orElseThrow(() -> new Exception("Veiculo não encntrado"));

		if(!vehicle.getUser().getId().equals(idLong)) {
			throw new Exception("Não permitido pois esse veiculo não opertence a esse usuario");
		}

		Boolean isExistPlaca=  validatePlacaExists(vehicleDTO.placa() ,idLong);
		if(isExistPlaca) {
			vehicle.setPlaca(vehicleDTO.placa());

		}
		if(vehicleDTO.vagas_disponiveis() <= 0) {
			throw new RuntimeException("Vagas não podem ser iguais ou menor a 0");

		}
		vehicle.setModelo(vehicleDTO.modelo());
		vehicle.setMarca(vehicleDTO.marca());
		vehicle.setCor(vehicleDTO.cor());
		vehicle.setAno(vehicleDTO.ano());
		vehicle.setAvailableSeats(vehicleDTO.vagas_disponiveis());

		createVehicle(vehicle);

		return convertVehicleToDTO(vehicle);
	}

	public VehicleDTO postCarByDriver(Long idLong, VehicleDTO vehicleDTO) throws Exception {
		User user = userRepository.findById(idLong).orElseThrow(() -> new RuntimeException("usuario não encontrado"));

		if(user.getUserType().getNome().equals("passageiro")) {
			throw new Exception("usuarios tipo passageiro não são permitidos cadastrar carros");
		}
		if(vehicleDTO.vagas_disponiveis() <= 0) {
			throw new RuntimeException("Vagas não podem ser iguais ou menor a 0");

		}
		VehicleDTO newVehicleDTO =  cadastrarVehehicle(vehicleDTO,user);
		return newVehicleDTO;
	}


	public void deleteCarByDriver(Long idLong, Long id_veiculo) throws Exception {
		Vehicle vehicle = vehicleRepository.findById(id_veiculo).orElseThrow(() -> new Exception("Veiculo não encntrado"));

		if(!vehicle.getUser().getId().equals(idLong)) {
			throw new Exception("Não permitido pois esse veiculo não opertence a esse usuario");
		}

		try {
			vehicleRepository.delete(vehicle);
		} catch (Exception e) {
			 new RuntimeException("Erro ao tentar deletar carro", e);
		}
	}


}
