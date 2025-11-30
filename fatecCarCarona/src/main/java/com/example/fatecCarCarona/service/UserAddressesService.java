package com.example.fatecCarCarona.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.dto.OpenstreetmapDTO;
import com.example.fatecCarCarona.dto.UserAddressesDTO;
import com.example.fatecCarCarona.dto.UserAddressesResponseDTO;
import com.example.fatecCarCarona.dto.ViaCepDTO;
import com.example.fatecCarCarona.entity.City;
import com.example.fatecCarCarona.entity.User;
import com.example.fatecCarCarona.entity.UserAddresses;
import com.example.fatecCarCarona.repository.UserAddressesRepository;

@Service
public class UserAddressesService {


	@Autowired
	UserAddressesRepository userAddressesRepository;
	@Autowired
	CityService cityService;
	@Autowired
	OpenstreetmapService openstreetmapService;
	@Autowired
	ViaCepService viaCepService;

	public Optional<OpenstreetmapDTO> buscar(String local) {

		return openstreetmapService.buscarLocal(local);
	}


	public UserAddresses createUserAddresses(UserAddresses userAddresses) {
		return  userAddressesRepository.save(userAddresses);
	}


	public UserAddressesDTO convertUserAddressesToDTO(UserAddresses saved) {

	    return new UserAddressesDTO(
	        saved.getCity().getId(),
	        saved.getLogradouro(),
	        saved.getNumero(),
	        saved.getBairro(),
	        saved.getCep()
	    );
	}


	public UserAddresses convertDTOTOUserAddresses(UserAddressesDTO userAddressesDTO, User user, City city,String latitude, String longitude ) {
	    UserAddresses userAddresses = new UserAddresses();
	    userAddresses.setUser(user);
	    userAddresses.setCity(city);
	    userAddresses.setLogradouro(userAddressesDTO.logradouro());
	    userAddresses.setNumero(userAddressesDTO.numero());
	    userAddresses.setBairro(userAddressesDTO.bairro());
	    userAddresses.setCep(userAddressesDTO.cep());
	    userAddresses.setLatitude(latitude);
	    userAddresses.setLongitude(longitude);

		return userAddresses;
	}



	public UserAddresses validateUserAddressesViaCep(User user, UserAddressesDTO userAddressesDTO ) {
	    City city = cityService.validateCity(userAddressesDTO.cityId());

	    Optional<ViaCepDTO> viaCepDTO = viaCepService.buscarCep(userAddressesDTO.cep());
		if(viaCepDTO.isEmpty()) {
	        throw new IllegalArgumentException("CEP destino não encontrado.");

		}
		//boolean isValid =
		//		viaCepDTO.get().localidade().equals(city.getNome()) &&
		//		viaCepDTO.get().logradouro().equals(userAddressesDTO.logradouro()) &&
		//		viaCepDTO.get().bairro().equals(userAddressesDTO.bairro()) ;

		//if(!isValid) {
	     //   throw new IllegalArgumentException("Endereço não corresponde ao CEP.");

		//}
	    String localString =
	    	    userAddressesDTO.logradouro() + " " +
	    	    city.getNome();


	    System.out.println(localString);


	    Optional<OpenstreetmapDTO> resultado = buscar(localString);
	    if (resultado.isEmpty()) {
	        throw new RuntimeException("Erro ao buscar endereço: Endereco não encontrado");
	    }


	    UserAddresses userAddresses = convertDTOTOUserAddresses(userAddressesDTO,user, city,resultado.get().lat(),resultado.get().lon());

	    return userAddresses;

	}



	public UserAddressesDTO cadastrarUserAddresses(UserAddressesDTO userAddressesDTO, User user) {
		UserAddresses address = validateUserAddressesViaCep(user, userAddressesDTO);
        UserAddresses saved = createUserAddresses(address);
        return convertUserAddressesToDTO(saved);
	}



	public UserAddressesResponseDTO getMyAddresses(Long idLong) {
		UserAddresses address = userAddressesRepository.getByUser(idLong);
		UserAddressesResponseDTO userAddressesResponseDTO = new UserAddressesResponseDTO(
				address.getId(),
				address.getCity().getNome(),
				address.getLogradouro(),
				address.getNumero(),
				address.getBairro(),
				address.getCep()
				);
		return userAddressesResponseDTO;
	}


	public UserAddressesDTO updateMyAddresses(Long idLong, Long idAddres,UserAddressesDTO userAddressesDTO ) throws Exception {
		UserAddresses addresses = userAddressesRepository.findById(idAddres).orElseThrow(() -> new RuntimeException("endereço não encontrado"));

		if(!addresses.getUser().getId().equals(idLong)) {
			throw new Exception("Esse endereçõ não está associado a esse usuario");

		}

	    City city = cityService.validateCity(userAddressesDTO.cityId());

	    Optional<ViaCepDTO> viaCepDTO = viaCepService.buscarCep(userAddressesDTO.cep());

		if(viaCepDTO.isEmpty()) {
	        throw new IllegalArgumentException("CEP não encontrado.");

		}
		//boolean isValid =
		//		viaCepDTO.get().localidade().equals(city.getNome()) &&
		//		viaCepDTO.get().logradouro().equals(userAddressesDTO.logradouro()) &&
		//		viaCepDTO.get().bairro().equals(userAddressesDTO.bairro()) ;

		//if(!isValid) {
	     //   throw new IllegalArgumentException("Endereço não corresponde ao CEP.");

		//}

		addresses.setCep(city.getNome());
		addresses.setLogradouro(userAddressesDTO.logradouro());
		addresses.setNumero(userAddressesDTO.numero());
		addresses.setBairro(userAddressesDTO.bairro());
		addresses.setCep(userAddressesDTO.cep());

		String localString =
	    	    userAddressesDTO.logradouro() + " " +
	    	    city.getNome();


	    Optional<OpenstreetmapDTO> resultado = buscar(localString);
	    if (resultado.isEmpty()) {
	        throw new RuntimeException("Erro ao buscar endereço: Endereco não encontrado");
	    }


	    userAddressesRepository.save(addresses);
	    UserAddressesDTO addressesDTO =  convertUserAddressesToDTO(addresses);


		return addressesDTO;
	}
}
