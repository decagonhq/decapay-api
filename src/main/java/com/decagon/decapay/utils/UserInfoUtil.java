package com.decagon.decapay.utils;

import com.decagon.decapay.exception.UnAuthorizedException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserInfoUtil {

    private User user;
    @Autowired
    private UserService userService;
    public User getCurrAuthUser() {
        if (user != null) {
            return user;
        }
        String userName = this.getCurrentAuthUserName();
        if (userName != null) {
            Optional<User> optionalUser = this.userService.findUserByEmail(userName);
            if(optionalUser.isPresent()){
                user=optionalUser.get();
                return user;
            }
        }
        throw new UnAuthorizedException("");
    }

    private String getCurrentAuthUserName() {
        String currUserName = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (Objects.nonNull(securityContext)) {
            Authentication authentication = securityContext.getAuthentication();
            if (Objects.nonNull(authentication)) {
                if (authentication.getPrincipal() instanceof String) {
                    currUserName = (String) authentication.getPrincipal();
                }
            }
        }
        return currUserName;
    }


}
