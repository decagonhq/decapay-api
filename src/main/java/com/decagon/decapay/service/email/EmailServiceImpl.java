package com.decagon.decapay.service.email;

import com.decagon.decapay.config.email.EmailConfig;
import com.decagon.decapay.payloads.Email;
import com.decagon.decapay.service.system.SystemConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.decagon.decapay.constants.ResponseMessageConstants.ERROR_WHILE_PARSING_EMAIL_CONFIG;
import static com.decagon.decapay.constants.SchemaConstants.EMAIL_CONFIG;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailSender emailSender;
    private final SystemConfigService systemConfigService;


    @Override
    @Async
    public void sendAsyncEmail(Email email) {

        EmailConfig emailConfig = this.getEmailConfiguration();
        if (emailConfig != null) {
            this.emailSender.setEmailConfig(emailConfig);
        }
        this.emailSender.send(email);
    }

    private EmailConfig getEmailConfiguration() {

        String value = this.systemConfigService.findConfigValueByKey(EMAIL_CONFIG);
        ObjectMapper mapper = new ObjectMapper();
        EmailConfig emailConfig = null;
        try {
            emailConfig = mapper.readValue(value, EmailConfig.class);
        } catch (Exception e) {
            log.error(ERROR_WHILE_PARSING_EMAIL_CONFIG, e);
        }
        return emailConfig;
    }


}
