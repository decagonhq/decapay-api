package com.decagon.decapay.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserInfo implements UserDetails {

    private String password;
    private String userName;
    private boolean enabled;

    public UserInfo(String userName,String password,boolean enabled) {
        this.userName=userName;
        this.password=password;
        this.enabled=enabled;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getGrantedAuthorities(Collections.emptyList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        privileges.forEach((privilege) -> {
            authorities.add(new SimpleGrantedAuthority(privilege));
        });
        return authorities;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}