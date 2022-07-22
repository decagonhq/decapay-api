package com.decagon.decapay.service;


import com.decagon.decapay.dto.AuthResponse;
import com.decagon.decapay.dto.LoginDto;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repository.UserRepository;
import com.decagon.decapay.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;

import static com.decagon.decapay.utils.ResourceHelper.validateResourceExists;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(email);
        user.ifPresent(presentUser -> presentUser.setAccountNonLocked(true));
        return validateResourceExists(user, "User Not Found!!");
    }
}
