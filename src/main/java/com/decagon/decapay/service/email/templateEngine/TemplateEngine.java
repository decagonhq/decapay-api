package com.decagon.decapay.service.email.templateEngine;


import java.util.Map;

public interface TemplateEngine {
    String processTemplateIntoString(String templateName, Map<String,String> templateTokens);

}
