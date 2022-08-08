package com.decagon.decapay.service.auth;


import com.decagon.decapay.model.auth.TokenBlacklist;
import com.decagon.decapay.repositories.auth.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;


    @Override
    public void blackListToken(String token){
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(token);
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    @Override
    public boolean isTokenBlacklisted(String token){
        return tokenBlacklistRepository.existsByToken(token);
    }
}
