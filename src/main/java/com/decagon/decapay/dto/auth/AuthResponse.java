package com.decagon.decapay.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuthResponse {
    private String token;
    private String country;
    private String language;
    private String currency;


    public AuthResponse(String jwt) {
        this.token = jwt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
