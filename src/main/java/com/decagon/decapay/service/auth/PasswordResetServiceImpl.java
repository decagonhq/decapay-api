package com.decagon.decapay.service.auth;

import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.password.PasswordReset;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.repositories.auth.PasswordResetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.EmailTemplateUtil;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.decagon.decapay.constants.AppConstants.*;
import static com.decagon.decapay.constants.AppConstants.USER_URI;
import static com.decagon.decapay.constants.ResponseMessageConstants.EMAIL_IS_EMPTY;
import static com.decagon.decapay.constants.ResponseMessageConstants.USER_NOT_FOUND;
import static com.decagon.decapay.utils.CommonUtil.generateOTP;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService{
    @Value("${api.url-domain}")
    private String urlDomain;
    private final PasswordResetRepository repository;
    private final UserRepository userRepository;
    private final EmailTemplateUtil emailTemplateUtil;
    @Override
    public void publishForgotPasswordResetEmail(String email) {
        if (email == null) {
            throw new InvalidRequestException(EMAIL_IS_EMPTY);
        }
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        final String token = UUID.randomUUID().toString();

        this.createPasswordResetEntity(token, WEB_DEVICE_ID,email);
        String passwordResetUrl = this.createPasswordResetUrl(token);
        this.publishPasswordResetEmail(user, passwordResetUrl);
    }


    @Override
    public void publishForgotPasswordResetCodeEmail(String email) {
        if (email == null) {
            throw new InvalidRequestException(EMAIL_IS_EMPTY);
        }
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        String code = "";
        try {
            code = String.valueOf(generateOTP());
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating OTP", e);
        }
        this.createPasswordResetEntity(code, ANDROID_DEVICE_ID, email);
        this.publishPasswordResetEmailForAndroid(user, code);
    }

    private void publishPasswordResetEmailForAndroid(User user, String code) {
        this.emailTemplateUtil.sendPasswordResetEmailForAndroid(user, code);
    }



    private void createPasswordResetEntity(String token, String deviceId, String email) {
        PasswordReset passwordReset = PasswordReset.builder()
                .deviceId(deviceId)
                .email(email)
                .token(token)
                .build();
        passwordReset.calculateTokenExpiryDate(String.valueOf(PASSWORD_RESET_TOKEN_VALIDITY_PERIOD));
        this.repository.save(passwordReset);
    }

    private void publishPasswordResetEmail(User user, String passwordResetUrl) {
        this.emailTemplateUtil.sendPasswordResetEmail(user, passwordResetUrl);
    }

    private String createPasswordResetUrl(String token) {
        return String.format("%s%s%s/%s",urlDomain,PASSWORD_RESET_URI,USER_URI,token);
    }
}
