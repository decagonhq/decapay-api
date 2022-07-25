package com.decagon.decapay.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.decagon.decapay.DTO.UserDTO;
import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.service.UserService;

import com.decagon.decapay.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UserServiceImpl(final UserRepository userRepository, final BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public ApiDataResponse<User> registerUser(final UserDTO userDTO) {
		ApiDataResponse<User> response = new ApiDataResponse<>();

		if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
			response.setMessage("The user email exists already");
			response.setStatus(HttpStatus.valueOf(400));
			return response;
		}

		try {
			User user = User.builder().firstName(userDTO.getFirstName())
				.lastName(userDTO.getLastName())
				.email(userDTO.getEmail())
				.password(passwordEncoder.encode(userDTO.getPassword()))
				.phoneNumber(userDTO.getPhoneNumber()).build();

			User registeredUser = userRepository.save(user);
			response.setData(registeredUser);
			response.setMessage("Successfully registered.");
			response.setStatus(HttpStatus.valueOf(201));
		} catch (Exception ex) {
			response.setMessage(ex.getMessage());
			response.setStatus(HttpStatus.valueOf(400));
		}
		return response;
	}
}
