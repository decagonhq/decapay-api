package com.decagon.decapay.service;

import org.springframework.stereotype.Service;

import com.decagon.decapay.DTO.UserDTO;
import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.model.user.User;

@Service
public interface UserService {
	ApiDataResponse<User> registerUser(UserDTO userDTO);
}
