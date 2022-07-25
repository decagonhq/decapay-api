package com.decagon.decapay.service.email;

import com.decagon.decapay.config.email.EmailConfig;
import com.decagon.decapay.payloads.request.email.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailSender emailSender;
    private final Environment env;

    @Override
    @Async
    public void sendAsyncEmail(Email email) {

        EmailConfig emailConfig = this.getEmailConfiguration();
        this.emailSender.setEmailConfig(emailConfig);
        this.emailSender.send(email);
    }

    private EmailConfig getEmailConfiguration() {
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost(env.getProperty("spring.mail.host"));
        emailConfig.setProtocol(env.getProperty("spring.mail.properties.mail.transport.protocol"));
        emailConfig.setPort(env.getProperty("spring.mail.properties.mail.smtp.port"));
        emailConfig.setSmtpAuth(Objects.equals(env.getProperty("spring.mail.properties.mail.smtp.auth"), "true"));
        emailConfig.setStarttls(Objects.equals(env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"), "true"));
        emailConfig.setUsername(env.getProperty("spring.mail.username"));
        emailConfig.setPassword(env.getProperty("spring.mail.password"));
        return emailConfig;
    }


}
