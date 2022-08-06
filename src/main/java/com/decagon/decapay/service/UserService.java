package com.decagon.decapay.service;

import com.decagon.decapay.DTO.UserDTO;
import org.springframework.stereotype.Service;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.model.user.User;

@Service
public interface UserService {
	User registerUser(UserDTO userDTO);
}
