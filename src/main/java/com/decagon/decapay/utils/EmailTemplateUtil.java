package com.decagon.decapay.utils;


import com.decagon.decapay.payloads.Email;
import com.decagon.decapay.service.email.templateEngine.TemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.decagon.decapay.constants.EmailConstants.*;
import static com.decagon.decapay.constants.SchemaConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailTemplateUtil {
    private final TemplateEngine templateEngine;

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

}
