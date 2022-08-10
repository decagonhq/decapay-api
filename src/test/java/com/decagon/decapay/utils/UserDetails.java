package com.decagon.decapay.utils;

import com.decagon.decapay.model.user.User;
import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.security.CustomUserDetailsService;
import com.decagon.decapay.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class UserDetails {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private User user;

    public User getUser(String email, String phone) {
        user = new User();
        user.setLastName("John");
        user.setFirstName("Doe");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setUserStatus(UserStatus.ACTIVE);
        user.setPhoneNumber(phone);
        return userRepository.save(user);
    }

    @Transactional
    public String getLoggedInToken(String email) {
//        user = this.getUser(email,phone);
        return tokenProvider.generateToken(userDetailsService.loadUserByUsername(email));
    }
}
