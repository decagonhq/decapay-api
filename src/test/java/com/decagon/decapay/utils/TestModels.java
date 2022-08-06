package com.decagon.decapay.utils;

import com.decagon.decapay.model.password.PasswordReset;
import com.decagon.decapay.model.user.User;


public class TestModels {

    public static User user(String firstName, String lastName, String email, String password, String phoneNo) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNo);
        return user;
    }

    public static PasswordReset passwordReset(String email, String token) {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(email);
        passwordReset.setToken(token);
        return passwordReset;
    }

}
