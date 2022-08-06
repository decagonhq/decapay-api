package com.decagon.decapay.utils;

import com.decagon.decapay.security.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserInfoUtills {

    public UserDetails authenticatedUser(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(Objects.nonNull(securityContext)){
            Authentication authentication = securityContext.getAuthentication();
            if (Objects.nonNull(authentication)){
                return (UserDetails) authentication.getPrincipal();
            }
        }
        return null;
    }


    public UserInfo authenticationUserInfo(){
        UserDetails userDetails = this.authenticatedUser();
        if (userDetails instanceof UserInfo userInfo){
            return userInfo;
        }
        return null;
    }

}
