package com.decagon.decapay.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.decagon.decapay.DTO.UserDTO;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;

@SpringBootTest
class UserServiceImplTest {

	@MockBean
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	UserServiceImpl userService;

	UserDTO userDTO;

	User user;

	@BeforeEach
	void setUp() {
		userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
		user = User.builder().firstName("firstName")
			.lastName("lastName")
			.email("a@b.com")
			.password("Password1!")
			.phoneNumber("0123456789").build();
	}

	@Test
	@DisplayName("Should return user already exist response if email already registered")
	void whenRegisteringWithExistingEmail() {
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
		assertThrows(ResourceConflictException.class, () -> userService.registerUser(userDTO));
	}


	@Test
	@DisplayName("Should register user successfully")
	void registerUser() {
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(user);

		User user1 = userService.registerUser(userDTO);

		assertEquals("firstName", user1.getFirstName());
		assertEquals("lastName", user1.getLastName());
		assertEquals("Password1!", user1.getPassword());
		assertEquals("a@b.com", user1.getEmail());
		assertEquals("0123456789", user1.getPhoneNumber());
	}
}