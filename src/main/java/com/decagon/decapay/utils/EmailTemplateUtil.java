package com.decagon.decapay.utils;


import com.decagon.decapay.model.user.User;
import com.decagon.decapay.payloads.Email;
import com.decagon.decapay.service.email.EmailService;
import com.decagon.decapay.service.email.templateEngine.TemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.decagon.decapay.constants.EmailConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.UNABLE_TO_SEND_EMAIL;
import static com.decagon.decapay.constants.SchemaConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailTemplateUtil {
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    private Email createEmail(String to, String subject, String template, Map<String, String> templateTokens) {
        Email email = new Email();
        email.setFrom(DEFAULT_COMPANY_NAME);
        email.setFromEmail(templateTokens.get(DEFAULT_COMPANY_EMAIL));
        email.setSubject(subject);
        email.setTo(to);
        email.setBody(this.templateEngine.processTemplateIntoString(template, templateTokens));
        return email;
    }

    private Map<String, String> createEmailObjectsMap() {
        String[] copyArg = {DEFAULT_COMPANY_NAME, CustomDateUtil.getPresentYear()};
        String[] supportEmailArg = {DEFAULT_COMPANY_EMAIL};

        Map<String, String> templateTokens = new HashMap<>();

        templateTokens.put(LOGO_PATH, "");
        templateTokens.put(EMAIL_FOOTER_COPYRIGHT, String.format("Copyright @ %s %s, All Rights Reserved",copyArg[0], copyArg[1]));
        templateTokens.put(EMAIL_DISCLAIMER, String.format(EMAIL_DISCLAIMER_MSG,supportEmailArg[0]));
        templateTokens.put(EMAIL_SPAM_DISCLAIMER, EMAIL_SPAM_DISCLAIMER_MSG);
        templateTokens.put(EMAIL_FROM_EMAIL, DEFAULT_COMPANY_EMAIL);

        return templateTokens;
    }

    //Sample Email Sending Method
    public void sendSampleEmail(User user) {
        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EMAIL_FULL_NAME, String.format("%s %s", user.getFirstName(), user.getLastName()));
        Email email = this.createEmail(user.getEmail(), CREATE_SAMPLE_EMAIL_SUBJ, SAMPLE_EMAIL_TMPL, templateTokens);
        this.sendEmail(email);
    }

    private void sendEmail(Email email){
        try {
            this.emailService.sendAsyncEmail(email);
        } catch (Exception e) {
            log.error(UNABLE_TO_SEND_EMAIL, e);
        }
    }

}
