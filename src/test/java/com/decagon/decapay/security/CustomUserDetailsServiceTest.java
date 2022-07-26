package com.decagon.decapay.security;

import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userService;

    @Test
    void loadUserByUsername() {
        String email = "og@gmail.com";

        User user = new User();
        user.setEmail(email);

        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        Mockito.verify(userRepository).findUserByEmail(user.getEmail());
        assertThat(userDetails).isNotNull();
        assertEquals(userDetails.getUsername(), user.getEmail());
    }
}