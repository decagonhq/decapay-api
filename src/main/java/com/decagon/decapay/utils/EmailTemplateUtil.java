package com.decagon.decapay.utils;


import com.decagon.decapay.exception.EmailException;
import com.decagon.decapay.model.user.User;
import com.decagon.decapay.payloads.Email;
import com.decagon.decapay.service.email.EmailService;
import com.decagon.decapay.service.email.templateEngine.TemplateEngine;
import com.decagon.decapay.service.system.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.decagon.decapay.constants.ApiConstants.LOGIN_URI;
import static com.decagon.decapay.constants.EmailConstants.*;
import static com.decagon.decapay.constants.ResponseMessageConstants.UNABLE_TO_SEND_EMAIL;
import static com.decagon.decapay.constants.SchemaConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailTemplateUtil {
    private final EmailService emailService;
    private final SystemConfigService systemConfigService;
    private final TemplateEngine templateEngine;
    @Value("${api.url-domain}")
    private String urlDomain;


    public void sendSuccessfulRegEmail(User user) {
        Map<String, String> templateTokens = this.createEmailObjectsMap();
        templateTokens.put(EMAIL_FULL_NAME, String.format("%s %s", user.getFirstName(), user.getLastName()));
        templateTokens.put(LOGIN_URL, String.format("%s%s",urlDomain, LOGIN_URI));
        String subject = this.systemConfigService.findConfigValueByKey(EMAIL_SUBJ_CREATE_USER_EMAIL);
        if (StringUtils.isEmpty(subject)) {
            subject = DEFAULT_CREATE_USER_EMAIL_SUBJ;
            log.warn("Email subject is empty. Using default subject: {}", subject);
        }
        Email email = this.createEmail(user.getEmail(), subject, EMAIL_CREATE_USER_TMPL, templateTokens);

        try {
            this.emailService.sendAsyncEmail(email);
        } catch (Exception e) {
            log.error(UNABLE_TO_SEND_EMAIL, e);
            throw new EmailException(UNABLE_TO_SEND_EMAIL, e);
        }
    }

    private Email createEmail(String to, String subject, String template, Map<String, String> templateTokens) {
        Email email = new Email();
        String companyName = this.systemConfigService.findConfigValueByKey(COMPANY_NAME);
        if (StringUtils.isEmpty(companyName)) {
            companyName = DEFAULT_COMPANY_NAME;
            log.warn("Company name is empty. Using default company name: {}", companyName);
        }
        email.setFrom(companyName);
        email.setFromEmail(templateTokens.get(EMAIL_FROM_EMAIL));
        email.setSubject(subject);
        email.setTo(to);
        email.setBody(this.templateEngine.processTemplateIntoString(template, templateTokens));
        return email;
    }

    private Map<String, String> createEmailObjectsMap() {

        String companyName = systemConfigService.findConfigValueByKey(COMPANY_NAME);
        if(StringUtils.isEmpty(companyName)){
            log.warn("No Company name found in configuration,using default application defined value");
            companyName=DEFAULT_COMPANY_NAME;
        }
        String[] copyArg = {companyName, CustomDateUtil.getPresentYear()};

        String supportEmail = systemConfigService.findConfigValueByKey(SUPPORT_EMAIL);
        if(StringUtils.isEmpty(supportEmail)){
            log.warn("No Support email found in configuration,using default application defined value");
            supportEmail=DEFAULT_SUPPORT_EMAIL;
        }
        String[] supportEmailArg = {supportEmail};

        Map<String, String> templateTokens = new HashMap<>();

        templateTokens.put(LOGO_PATH, "");
        templateTokens.put(EMAIL_FOOTER_COPYRIGHT, String.format("Copyright @ %s %s, All Rights Reserved",copyArg[0], copyArg[1]));
        templateTokens.put(EMAIL_DISCLAIMER, String.format(EMAIL_DISCLAIMER_MSG,supportEmailArg[0]));
        templateTokens.put(EMAIL_SPAM_DISCLAIMER, EMAIL_SPAM_DISCLAIMER_MSG);
        templateTokens.put(EMAIL_FROM_EMAIL, supportEmail);

        return templateTokens;
    }

}
