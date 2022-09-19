package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.user.UserResponseDto;
import com.decagon.decapay.dto.auth.ChangePasswordRequestDto;
import com.decagon.decapay.dto.common.IdResponseDto;

import com.decagon.decapay.dto.user.UserDTO;
import com.decagon.decapay.model.user.User;

import java.util.Optional;

public interface UserService {
	IdResponseDto registerUser(UserDTO userDTO);

    Optional<User> findUserByEmail(String userName);

    UserSettings getUserSettings();

    UserResponseDto viewUserProfile();

    void changePassword(ChangePasswordRequestDto changePasswordRequestDto, String token);
}
