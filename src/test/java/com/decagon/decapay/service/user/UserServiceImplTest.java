package com.decagon.decapay.service.user;

import com.decagon.decapay.dto.UserDTO;
import com.decagon.decapay.dto.common.IdResponseDto;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

		UserDTO userDTO = new UserDTO("firstName", "lastName", "a@b.com", "Password1!", "0123456789");
		AtomicReference<User> savedUser=new AtomicReference<>();
		when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
		when(this.passwordEncoder.encode(anyString())).thenReturn("encodedPass");
		when(userRepository.save(any())).thenReturn(user);
		when(this.userRepository.save(any())).thenAnswer((Answer<User>) i -> {
			savedUser.set(i.getArgument(0));
			savedUser.get().setId(1L);
			return savedUser.get();
		});
		IdResponseDto idResponseDto = userService.registerUser(userDTO);
		User user1=savedUser.get();

		assertEquals(idResponseDto.getId(),user1.getId());
		assertEquals("firstName", user1.getFirstName());
		assertEquals("lastName", user1.getLastName());
		assertEquals("encodedPass", user1.getPassword());
		assertEquals("a@b.com", user1.getEmail());
		assertEquals("0123456789", user1.getPhoneNumber());
	}
}