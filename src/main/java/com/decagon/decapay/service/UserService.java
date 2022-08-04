package com.decagon.decapay.service;

import org.springframework.stereotype.Service;

import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.user.User;

@Service
public interface UserService {
	User registerUser(UserDTO userDTO);
}
