package com.decagon.decapay.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.decagon.decapay.DTO.UserDTO;
import com.decagon.decapay.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	UserServiceImpl userService;

	UserDTO userDTO;

	User user;

	@BeforeEach
	void setUp() {
		userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
        user=new User();
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("a@b.com");
		user.setPassword("Password1!");
		user.setPhoneNumber("0123456789");

	}

	@Test
	@DisplayName("Should return user already exist response if email already registered")
	void whenRegisteringWithExistingEmail() {
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
		assertThrows(ResourceConflictException.class, () -> userService.registerUser(userDTO));
	}


	@Test
	@DisplayName("Should register user successfully")
	void registerUser() {
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(user);

		User user1 = userService.registerUser(userDTO);

		assertEquals("firstName", user1.getFirstName());
		assertEquals("lastName", user1.getLastName());
		assertEquals("Password1!", user1.getPassword());
		assertEquals("a@b.com", user1.getEmail());
		assertEquals("0123456789", user1.getPhoneNumber());
	}
}