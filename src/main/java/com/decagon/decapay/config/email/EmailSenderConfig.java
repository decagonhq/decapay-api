package com.decagon.decapay.config.email;

import com.decagon.decapay.service.email.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailSenderConfig {

    @Value("${config.emailSender}")
    private String emailSender;

    @Bean
    public EmailSender emailSender(ApplicationContext context) {

        return switch (emailSender) {
            case "default" -> (EmailSender) context.getBean("defaultEmailSenderImpl");
            case "cloud" -> (EmailSender) context.getBean("sesEmailSender");
            case "cloud-smtp" -> (EmailSender) context.getBean("sesSMTPEmailSender");
            default -> (EmailSender) context.getBean("defaultEmailSenderImpl");
        };
    }
}
