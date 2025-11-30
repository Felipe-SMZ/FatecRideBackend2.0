package com.example.fatecCarCarona.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fatecCarCarona.entity.User;
import com.example.fatecCarCarona.repository.UserRepository;
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user =  userRepository.findById(Long.parseLong(username)).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getSenha(), new ArrayList<>());


	}

}
