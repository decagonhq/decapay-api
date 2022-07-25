package com.decagon.decapay.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.decagon.decapay.DTO.UserDTO;
import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repository.UserRepository;

@SpringBootTest
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	UserServiceImpl userService;

	UserDTO userDTO;

	User user;

	@BeforeEach
	void setUp(){
		userDTO = new UserDTO("firstName","lastName","a@b.com","Password1!", "0123456789");

		user = User.builder().firstName("firtName")
			.lastName("lastName")
			.email("a@b.com")
			.password(passwordEncoder.encode("Password1!"))
			.phoneNumber("0123456789").build();
	}

	@Test
	@DisplayName("Should return user already exist response if email already registered")
	void whenRegisteringWithExistingEmail(){
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

		ApiDataResponse<User> response = userService.registerUser(userDTO);

		assertEquals("The user email exists already", response.getMessage());
		assertEquals(HttpStatus.valueOf(400), response.getStatus());
	}


	@Test
	void registerUser() {
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(null);

		ApiDataResponse<User> response = userService.registerUser(userDTO);

		assertEquals("Successfully registered.", response.getMessage());
		assertEquals(HttpStatus.valueOf(201), response.getStatus());
		User user1 = response.getData();

		assertEquals("firstName", user1.getFirstName());
		assertEquals("lastName", user1.getLastName());
		assertTrue(passwordEncoder.matches("Password1!", user1.getPassword()));
		assertEquals("a@b.com", user1.getEmail());
		assertEquals("0123456789", user1.getPhoneNumber());
		assertNotNull(user1.getId());
	}
}