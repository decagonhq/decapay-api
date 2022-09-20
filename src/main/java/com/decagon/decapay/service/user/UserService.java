package com.decagon.decapay.service.user;

import com.decagon.decapay.config.userSetting.UserSettings;
import com.decagon.decapay.dto.user.UserDto;
import com.decagon.decapay.dto.user.UserResponseDto;
import com.decagon.decapay.dto.user.ChangePasswordRequestDto;
import com.decagon.decapay.dto.common.IdResponseDto;

import com.decagon.decapay.dto.user.SignUpRequestDTO;
import com.decagon.decapay.model.user.User;

import java.util.Optional;

public interface UserService {
	IdResponseDto registerUser(SignUpRequestDTO signUpRequestDTO);

    Optional<User> findUserByEmail(String userName);

    UserSettings getUserSettings();

    UserResponseDto viewUserProfile();

    User updateUserProfile(UserDto userDto);

    void changePassword(ChangePasswordRequestDto changePasswordRequestDto, String token);
}
