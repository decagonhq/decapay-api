package com.decagon.decapay.service.impl;

import com.decagon.decapay.DTO.UserDTO;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(final UserDTO userDTO) throws ResourceConflictException {

        if (userRepository.findByEmail(userDTO.getEmail().toLowerCase()).isPresent()) {
            throw new ResourceConflictException();
        }

		/*User user = User.builder().firstName(userDTO.getFirstName())
			.lastName(userDTO.getLastName())
			.email(userDTO.getEmail().toLowerCase())
			.password(passwordEncoder.encode(userDTO.getPassword()))
			.phoneNumber(userDTO.getPhoneNumber()).build();*/

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());

        return userRepository.save(user);
    }
}
