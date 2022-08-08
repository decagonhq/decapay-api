package com.decagon.decapay.service.auth;

public interface TokenBlacklistService {
    void blackListToken(String token);

    boolean isTokenBlacklisted(String token);
}
