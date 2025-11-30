package com.example.fatecCarCarona.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.fatecCarCarona.dto.ExceptionalDTO;

@ControllerAdvice
public class ControlExceptionalHeandler {


		@ExceptionHandler(Exception.class)
		public ResponseEntity theatGeneralExceptional(Exception exception) {
			ExceptionalDTO exceptionalDTO = new ExceptionalDTO(exception.getMessage(), "500");
			return ResponseEntity.internalServerError().body(exceptionalDTO);
		}
	}

