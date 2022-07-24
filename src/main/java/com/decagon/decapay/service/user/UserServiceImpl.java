package com.decagon.decapay.service.user;

import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.EmailTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.decagon.decapay.constants.AppConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${api.url-domain}")
    private String urlDomain;
    private final UserRepository repository;
    private final EmailTemplateUtil emailTemplateUtil;
    @Override
    public void publishForgotPasswordEmail(String email) {
        User user = this.repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        final String token = UUID.randomUUID().toString();
        this.createPasswordResetTokenForUser(user, token);
        String passwordResetUrl = this.createPasswordResetUrl(token);
        this.publishPasswordResetEmail(user, passwordResetUrl);
    }


    private void publishPasswordResetEmail(User user, String passwordResetUrl) {
        this.emailTemplateUtil.sendPasswordResetEmail(user, passwordResetUrl);
    }

    private String createPasswordResetUrl(String token) {
            return String.format("%s%s%s/%s",urlDomain,PASSWORD_RESET_URI,USER_URI,token);
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        user.setPasswordResetToken(token);
        String valdtyTrm = String.valueOf(PASSWORD_SETTING_VLDTY_TRM);
        user.calculateTokenExpiryDate(valdtyTrm);
        this.repository.save(user);
    }
}
