package com.example.fatecCarCarona.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fatecCarCarona.dto.UserTypeDTO;
import com.example.fatecCarCarona.service.UserTypeService;

@RestController
@RequestMapping("/userType")
public class UserTypeController {

	@Autowired
	UserTypeService userTypeService;

	@GetMapping
	public ResponseEntity<List<UserTypeDTO>> alluserType(){
		List<UserTypeDTO> allUserType = userTypeService.allUserType();

		return ResponseEntity.ok(allUserType);
	}

}
