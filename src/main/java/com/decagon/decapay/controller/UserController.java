package com.decagon.decapay.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.decagon.decapay.DTO.UserDTO;
import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.service.UserService;

@RestController
@RequestMapping(path = "/users")
public class UserController {
	private final UserService userService;

	public UserController(final UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiDataResponse<User>> registerUser(@Valid @RequestBody UserDTO userRegistrationRequest) {
		ApiDataResponse<User> response = userService.registerUser(userRegistrationRequest);
		return new ResponseEntity<>(response, response.getStatus());
	}
}
