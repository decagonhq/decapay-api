package com.decagon.decapay.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.service.UserService;


@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User registerUser(final UserDTO userDTO) throws ResourceConflictException {

		if (userRepository.findByEmail(userDTO.getEmail().toLowerCase()).isPresent()) {
			throw new ResourceConflictException();
		}

		User user = User.builder().firstName(userDTO.getFirstName())
			.lastName(userDTO.getLastName())
			.email(userDTO.getEmail().toLowerCase())
			.password(passwordEncoder.encode(userDTO.getPassword()))
			.phoneNumber(userDTO.getPhoneNumber()).build();

		return userRepository.save(user);
	}
}
