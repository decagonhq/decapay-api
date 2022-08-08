package com.decagon.decapay.utils;


import com.decagon.decapay.model.user.User;
import com.decagon.decapay.service.email.Email;
import com.decagon.decapay.service.email.EmailService;
import com.decagon.decapay.service.email.templateEngine.TemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.decagon.decapay.constants.AppConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailTemplateUtil {
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    private Email createEmail(String to, String subject, String template, Map<String, String> templateTokens) {
        return Email.builder()
                .from(DEFAULT_COMPANY_NAME)
                .fromEmail(templateTokens.get(EMAIL_FROM_EMAIL))
                .subject(subject)
                .to(to)
                .body(this.templateEngine.processTemplateIntoString(template, templateTokens))
                .build();
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

    public void sendPasswordResetEmail(User user, String passwordResetUrl) {
        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EMAIL_PASSWORD_RESET_URL, passwordResetUrl);
        templateTokens.put(EMAIL_FULL_NAME, String.format("%s %s", user.getFirstName(), user.getLastName()));
        Email email = this.createEmail(user.getEmail(), EMAIL_SUBJ_PASSWORD_RESET_EMAIL, EMAIL_PASSWORD_RESET_TMPL, templateTokens);
        this.sendEmail(email);
    }

    private void sendEmail(Email email){
            this.emailService.sendAsyncEmail(email);
    }

    public void sendPasswordResetEmailForMobile(User user, String code) {
        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EMAIL_PASSWORD_RESET_CODE, code);
        templateTokens.put(EMAIL_FULL_NAME, String.format("%s %s", user.getFirstName(), user.getLastName()));
        Email email = this.createEmail(user.getEmail(), EMAIL_SUBJ_PASSWORD_RESET_EMAIL, EMAIL_PASSWORD_RESET_MOBILE_TMPL, templateTokens);
        this.sendEmail(email);

    }
}
