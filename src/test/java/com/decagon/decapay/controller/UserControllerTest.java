package com.decagon.decapay.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.decagon.decapay.DTO.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	UserDTO userDTO;

	ObjectMapper objectMapper = new ObjectMapper();


	@BeforeEach
	void setUp() {
		userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
	}

	@Test
	void registerUser() throws Exception {
		mockMvc.perform(
			post("/users/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(userDTO))).andExpect(status().is(201));
	}

	@Test
	void registerUserFails() throws Exception {
		mockMvc.perform(
			post("/users/register").contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(new UserDTO()))).andExpect(status().is(400));
	}
}
