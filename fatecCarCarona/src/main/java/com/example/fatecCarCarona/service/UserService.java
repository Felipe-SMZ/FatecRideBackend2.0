package com.example.fatecCarCarona.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.dto.UserAddressesDTO;
import com.example.fatecCarCarona.dto.UserBaseDTO;
import com.example.fatecCarCarona.dto.UserDTO;
import com.example.fatecCarCarona.dto.UserDriverDTO;
import com.example.fatecCarCarona.dto.VehicleDTO;
import com.example.fatecCarCarona.entity.Course;
import com.example.fatecCarCarona.entity.Gender;
import com.example.fatecCarCarona.entity.User;
import com.example.fatecCarCarona.entity.UserType;
import com.example.fatecCarCarona.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	GenderService genderService;
	@Autowired
	CourseService courseService;
	@Autowired
	UserTypeService userTypeService;
	@Autowired
	UserAddressesService userAddressesService;
	@Autowired

	VehicleService vehicleService;
	@Autowired
	private  PasswordEncoder passwordEncoder;
	@Autowired
	private  TokenService tokenService;


	public Boolean existeEmail(String email,Long id_usuario ) throws Exception {

		Optional<User> existingEmail = userRepository.findByEmail(email);
		if(existingEmail.isPresent() && !existingEmail.get().getId().equals(id_usuario)) {
        	throw new Exception("Email já cadastrada por outro usuário");

		}
		return true;
	}

	public User createUser(User user){
		return userRepository.save(user);
	}


	public User convertDtoToUser(UserDriverDTO userDriverDTO) {
		Course course = courseService.validateCourse(userDriverDTO.courseId());
		Gender gender =  genderService.validateGender(userDriverDTO.genderId());
		UserType userType = userTypeService.validateUserType(userDriverDTO.userTypeId());
		User user = new User();
		user.setNome(userDriverDTO.nome());
		user.setSobrenome(userDriverDTO.sobrenome());
		user.setEmail(userDriverDTO.email());
		user.setSenha(userDriverDTO.senha());
		user.setTelefone(userDriverDTO.telefone());
		user.setFoto(userDriverDTO.foto());
		user.setUserType(userType);
		user.setGender(gender);
		user.setCourse(course);
		return user;
	}

	public User convertDtoToUser(UserDTO userDTO) {
		Course course = courseService.validateCourse(userDTO.courseId());
		Gender gender =  genderService.validateGender(userDTO.genderId());
		UserType userType = userTypeService.validateUserType(userDTO.userTypeId());
		User user = new User();
		user.setNome(userDTO.nome());
		user.setSobrenome(userDTO.sobrenome());
		user.setEmail(userDTO.email());
		user.setSenha(userDTO.senha());
		user.setTelefone(userDTO.telefone());
		user.setFoto(userDTO.foto());
		user.setUserType(userType);
		user.setGender(gender);
		user.setCourse(course);

		return user;
	}

	public UserDriverDTO convertUserDriverToUserDriverDto(User user,UserAddressesDTO userAddressesDTO, VehicleDTO  vehicleDTO) {
	    return new UserDriverDTO(
	        user.getNome(),
	        user.getSobrenome(),
	        user.getEmail(),
	        user.getSenha(),
	        user.getTelefone(),
	        user.getFoto(),
	        user.getUserType().getId(),
	        user.getGender().getId(),
	        user.getCourse().getId(),
	        userAddressesDTO,
	        vehicleDTO

	    );
	}

	public UserDTO convertUserToUserDto(User user,UserAddressesDTO userAddressesDTO) {
	    return new UserDTO(
	        user.getNome(),
	        user.getSobrenome(),
	        user.getEmail(),
	        user.getSenha(),
	        user.getTelefone(),
	        user.getFoto(),
	        user.getUserType().getId(),
	        user.getGender().getId(),
	        user.getCourse().getId(),
	        userAddressesDTO
	    );
	}

	public UserBaseDTO convertUserToUserBaseDto(User user) {
	    return new UserBaseDTO(
	        user.getNome(),
	        user.getSobrenome(),
	        user.getEmail(),
	        user.getSenha(),
	        user.getTelefone(),
	        user.getFoto(),
	        user.getUserType().getId(),
	        user.getGender().getId(),
	        user.getCourse().getId()

	    );
	}

	@Transactional(rollbackOn = Exception.class)
	public UserDriverDTO  cadastrarDrivers(UserDriverDTO userDriverDTO) throws Exception {

		User user  = convertDtoToUser(userDriverDTO);

		user.setSenha(passwordEncoder.encode(userDriverDTO.senha()));

		if (userDriverDTO.vehicleDTO() == null) {
            throw new Exception("É preciso cadastrar pelo menos um carro");
        }
		if (userDriverDTO.userAddressesDTO() == null) {
            throw new Exception("É preciso cadastrar endereço");
        }
		if(user.getUserType().getNome().equalsIgnoreCase("passageiro") ) {

			throw new Exception("usuario passageiros não podem cadastrar carros");
		}


			User newUser = this.createUser(user);
			UserAddressesDTO createUserAddresses = userAddressesService.cadastrarUserAddresses(userDriverDTO.userAddressesDTO() ,newUser);

			VehicleDTO vehicleDTO= vehicleService.cadastrarVehehicle(userDriverDTO.vehicleDTO(),newUser);

		    return convertUserDriverToUserDriverDto(newUser, createUserAddresses, vehicleDTO);

	}



	@Transactional(rollbackOn = Exception.class)
	public UserDTO  cadastrarUser(UserDTO userDTO) throws Exception {

		User user  = convertDtoToUser(userDTO);
		user.setSenha(passwordEncoder.encode(userDTO.senha()));

		if (userDTO.userAddressesDTO() == null) {
            throw new Exception("É preciso cadastrar endereço");
        }
		if(!user.getUserType().getNome().equalsIgnoreCase("passageiro")) {

			throw new Exception("se quiser ser passageiro escolha tipo passageiro");
		}

			User newUser = this.createUser(user);
			UserAddressesDTO createUserAddresses = userAddressesService.cadastrarUserAddresses(userDTO.userAddressesDTO() ,newUser);


		    return  convertUserToUserDto(newUser, createUserAddresses);

	}



	public UserBaseDTO putCarByDriver(Long idLong, UserBaseDTO userBaseDTO) throws Exception {
		User user = userRepository.findById(idLong).orElseThrow(() -> new RuntimeException("usuario não encontrado"));
		Course course = courseService.validateCourse(userBaseDTO.courseId());
		Gender gender =  genderService.validateGender(userBaseDTO.genderId());
		UserType userType = userTypeService.validateUserType(userBaseDTO.userTypeId());

		user.setNome(userBaseDTO.nome());
		user.setSobrenome(userBaseDTO.sobrenome());

		Boolean existEmail = existeEmail(userBaseDTO.email(), idLong);
		if(existEmail) {
			user.setEmail(userBaseDTO.email());
		}
		user.setTelefone(userBaseDTO.telefone());
		user.setSenha(userBaseDTO.senha());
		user.setFoto(userBaseDTO.foto());
		user.setUserType(userType);
		user.setGender(gender);
		user.setCourse(course);
		user.setSenha(passwordEncoder.encode(userBaseDTO.senha()));
		createUser(user);

		return convertUserToUserBaseDto(user);
	}



	public void deleteUser(Long idLong) throws Exception {
		User user = userRepository.findById(idLong).orElseThrow(() -> new Exception("Usuario não encontado"));

		try {
			userRepository.delete(user);
		} catch (Exception e) {
			throw new Exception("Erro ao deletar usuario"+ e);
		}

	}


	public UserBaseDTO getUser(Long idLong) throws Exception {
		User user = userRepository.findById(idLong).orElseThrow(() -> new Exception("Usuario não encontado"));
		return convertUserToUserBaseDto(user);

	}





}
