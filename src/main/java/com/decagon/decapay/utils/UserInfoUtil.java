package com.decagon.decapay.utils;

import com.decagon.decapay.security.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserInfoUtil {
    public UserDetails authenticatedUser() {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            Authentication auth = securityContext.getAuthentication();
            if (auth != null) {
                return (UserDetails) auth.getPrincipal();
            }
        }
        return null;
    }

    public UserInfo authenticatedUserInfo() {
        UserDetails userDetails = this.authenticatedUser();
        if (userDetails instanceof UserInfo userInfo) {
            return userInfo;
        }
        return null;
    }
}
