package com.decagon.decapay.service.auth;


import com.decagon.decapay.model.user.TokenBlacklist;
import com.decagon.decapay.repositories.auth.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;


    public void blackListToken(String token){
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(token);
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    public boolean isTokenBlacklisted(String token){
        return tokenBlacklistRepository.existsByToken(token);
    }
}
