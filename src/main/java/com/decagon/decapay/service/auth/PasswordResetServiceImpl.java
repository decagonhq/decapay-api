package com.decagon.decapay.service.auth;

import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.password.PasswordReset;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.payloads.request.auth.CreatePasswordRequestDto;
import com.decagon.decapay.payloads.request.auth.ForgotPasswordRequestDto;
import com.decagon.decapay.payloads.request.auth.VerifyPasswordResetCodeRequest;
import com.decagon.decapay.repositories.auth.PasswordResetRepository;
import com.decagon.decapay.repositories.user.UserRepository;
import com.decagon.decapay.utils.EmailTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import static com.decagon.decapay.constants.AppConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.*;
import static com.decagon.decapay.enumTypes.ResetCodeStatus.*;
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
    private final PasswordEncoder passwordEncoder;


    @Override
    public void publishForgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto, String deviceId) {
        switch (deviceId) {
            case MOBILE_DEVICE_ID -> this.publishForgotPasswordResetCodeEmail(forgotPasswordRequestDto.getEmail());
            case WEB_DEVICE_ID -> this.publishForgotPasswordResetEmail(forgotPasswordRequestDto.getEmail());
            default -> throw new InvalidRequestException(UNEXPECTED_VALUE + deviceId);
        }
    }

    @Override
    public void verifyPasswordResetCode(VerifyPasswordResetCodeRequest verifyPasswordResetCodeRequest, String deviceId) {
        if(verifyPasswordResetCodeRequest.getEmail() == null || verifyPasswordResetCodeRequest.getResetCode() == null) {
            throw new InvalidRequestException(NO_RESET_CODE_OR_EMAIL_PROVIDED);
        }

        if (!deviceId.equals(MOBILE_DEVICE_ID)) {
            throw new InvalidRequestException("Unexpected value: " + deviceId);
        }

        PasswordReset passwordReset = this.repository.findByEmailAndDeviceId(verifyPasswordResetCodeRequest.getEmail(), deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(PASSWORD_RESET_CODE_DOES_NOT_EXIST));

        if(!passwordReset.getToken().equals(verifyPasswordResetCodeRequest.getResetCode())) {
            throw new InvalidRequestException(INVALID_PASSWORD_RESET_CODE);
        }

        if(passwordReset.tokenExpired()) {
            throw new InvalidRequestException(PASSWORD_RESET_CODE_HAS_EXPIRED);
        }

        if (passwordReset.getStatus().equals(VERIFIED) || passwordReset.getStatus().equals(INVALID)) {
            throw new InvalidRequestException(PASSWORD_RESET_CODE_ALREADY_USED);
        }

        passwordReset.setStatus(VERIFIED);
        repository.save(passwordReset);
    }

    @Override
    public void createPassword(CreatePasswordRequestDto createPasswordRequestDto, String deviceId) {
        this.validatePassword(createPasswordRequestDto.getPassword(), createPasswordRequestDto.getConfirmPassword());

        switch (deviceId) {
            case MOBILE_DEVICE_ID -> this.createNewPasswordForMobile(createPasswordRequestDto);
            case WEB_DEVICE_ID -> this.createNewPasswordForWeb(createPasswordRequestDto);
            default -> throw new InvalidRequestException(UNEXPECTED_VALUE + deviceId);
        }
    }

    private void createNewPasswordForWeb(CreatePasswordRequestDto createPasswordRequestDto) {
        PasswordReset passwordReset = this.repository.findByToken(createPasswordRequestDto.getToken()).orElseThrow(() -> new ResourceNotFoundException(TOKEN_DOES_NOT_EXIST));
        if (passwordReset.tokenExpired()) {
            throw new InvalidRequestException(PASSWORD_RESET_TOKEN_HAS_EXPIRED);
        }
        User user = this.userRepository.findByEmail(passwordReset.getEmail()).orElseThrow(()->new ResourceNotFoundException(USER_NOT_FOUND));
        this.saveNewPassword(user, createPasswordRequestDto.getPassword());
        this.invalidateToken(passwordReset);
    }

    private void createNewPasswordForMobile(CreatePasswordRequestDto createPasswordRequestDto) {
        PasswordReset passwordReset = this.repository.findByToken(createPasswordRequestDto.getToken()).orElseThrow(() -> new ResourceNotFoundException(PASSWORD_RESET_CODE_DOES_NOT_EXIST));
        if (passwordReset.getStatus().equals(UNVERIFIED)){
            throw new InvalidRequestException(PASSWORD_RESET_CODE_IS_UNVERIFIED);
        }
        User user = this.userRepository.findByEmail(passwordReset.getEmail()).orElseThrow(()->new ResourceNotFoundException(USER_NOT_FOUND));
        this.saveNewPassword(user, createPasswordRequestDto.getPassword());
        this.invalidateToken(passwordReset);
    }

    private void saveNewPassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
    }

    private void invalidateToken(PasswordReset passwordReset){
        passwordReset.setStatus(INVALID);
        this.repository.save(passwordReset);
    }



    private void validatePassword(String password, String confirmPassword){
        if (!password.equals(confirmPassword)){
            throw new InvalidRequestException(PASSWORDS_DONT_MATCH);
        }
    }

    private void publishForgotPasswordResetEmail(String email) {
        if (email == null) {
            throw new InvalidRequestException(EMAIL_IS_EMPTY);
        }
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        final String token = UUID.randomUUID().toString();

        Optional<PasswordReset> passwordReset = this.repository.findByEmailAndDeviceId(email, WEB_DEVICE_ID);
        if(passwordReset.isPresent()){
            this.updatePasswordReset(passwordReset.get(), token);
        } else {
            this.createPasswordResetEntity(token, WEB_DEVICE_ID, email);
            String passwordResetUrl = this.createPasswordResetUrl(token);
            this.publishPasswordResetEmail(user, passwordResetUrl);
        }
    }

    private void updatePasswordReset(PasswordReset passwordReset, String token) {
        passwordReset.setToken(token);
        passwordReset.calculateTokenExpiryDate(String.valueOf(PASSWORD_RESET_TOKEN_VALIDITY_PERIOD));
        this.repository.save(passwordReset);
    }


    private void publishForgotPasswordResetCodeEmail(String email) {
        if (email == null) {
            throw new InvalidRequestException(EMAIL_IS_EMPTY);
        }
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        String code = "";
        try {
            code = String.valueOf(generateOTP());
            Optional<PasswordReset> passwordReset = this.repository.findByEmailAndDeviceId(email, MOBILE_DEVICE_ID);
            if (passwordReset.isPresent()) {
                this.updatePasswordReset(passwordReset.get(), code);
            } else {
                this.createPasswordResetEntity(code, MOBILE_DEVICE_ID, email);
                this.publishPasswordResetEmailForMobile(user, code);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating OTP", e);
        }
    }

    private void publishPasswordResetEmailForMobile(User user, String code) {
        try {
            this.emailTemplateUtil.sendPasswordResetEmailForMobile(user, code);
        } catch (Exception e) {
            log.error(UNABLE_TO_SEND_EMAIL, e);
        }
    }



    private void createPasswordResetEntity(String token, String deviceId, String email) {
        PasswordReset passwordReset = PasswordReset.builder()
                .deviceId(deviceId)
                .email(email)
                .token(token)
                .build();
        if (deviceId.equals(WEB_DEVICE_ID)) {
            passwordReset.calculateTokenExpiryDate(String.valueOf(PASSWORD_RESET_TOKEN_VALIDITY_PERIOD));
        } else {
            passwordReset.calculateTokenExpiryDate(String.valueOf(PASSWORD_RESET_CODE_VALIDITY_PERIOD));
        }
        this.repository.save(passwordReset);
    }

    private void publishPasswordResetEmail(User user, String passwordResetUrl) {
        try {
            this.emailTemplateUtil.sendPasswordResetEmail(user, passwordResetUrl);
        } catch (Exception e) {
            log.error(UNABLE_TO_SEND_EMAIL, e);
        }
    }

    private String createPasswordResetUrl(String token) {
        return String.format("%s%s%s/%s",urlDomain,PASSWORD_RESET_URI,USER_URI,token);
    }
}
