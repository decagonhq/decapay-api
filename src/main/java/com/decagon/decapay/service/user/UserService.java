package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.common.IdResponseDto;

import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.model.user.User;

import java.util.Optional;

public interface UserService {
	IdResponseDto registerUser(UserDTO userDTO);

    Optional<User> findUserByEmail(String userName);

    UserSettings getUserSettings();
}
