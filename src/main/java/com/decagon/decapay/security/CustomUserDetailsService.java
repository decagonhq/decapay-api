package com.decagon.decapay.security;


import com.decagon.decapay.model.user.UserStatus;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user found with username:" + email));
		return new UserInfo(user.getEmail(), user.getPassword(), user.getUserStatus().equals(UserStatus.ACTIVE));
	}

	public User getLoggedInUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();

		return userRepository.findUserByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("No user found with username:" + username));
	}
}
