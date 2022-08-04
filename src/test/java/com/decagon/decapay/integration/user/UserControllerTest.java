package com.decagon.decapay.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.service.UserService;
import com.decagon.decapay.utils.extensions.DBCleanerExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(DBCleanerExtension.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	UserService userService;

	UserDTO userDTO;

	ObjectMapper objectMapper = new ObjectMapper();

	@Value("${api.basepath-api}")
	private String path;

	@BeforeEach
	void setUp() {
		userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
	}

	@Test
	void registerUser() throws Exception {
		ResultActions response = mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(userDTO))).andExpect(status().is(201));

		User user = userRepository.findByEmail(userDTO.getEmail()).get();


		assertEquals("firstName", user.getFirstName());
		assertEquals("lastName", user.getLastName());
		assertTrue(passwordEncoder.matches("Password1!", user.getPassword()));
		assertEquals("a@b.com", user.getEmail());
		assertEquals("0123456789", user.getPhoneNumber());
		assertNotNull(user.getId());

	}

	@Test
	void registerUserFailsWithIncompleteDTO() throws Exception {
		mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(new UserDTO()))).andExpect(status().is(400));
	}

	@Test
	void registerUserFailsWhenUserAlreadyExists() throws Exception {

		User user = new User();
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("a@b.com");
		user.setPassword("Password1!");
		user.setPhoneNumber("0123456789");
		userRepository.save(user);

		mockMvc.perform(
			post(path + "/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(userDTO))).andExpect(status().is(409));
	}
}
