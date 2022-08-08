package com.decagon.decapay.service.user;

import org.springframework.stereotype.Service;

import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.user.User;

public interface UserService {
	User registerUser(UserDTO userDTO);
}
