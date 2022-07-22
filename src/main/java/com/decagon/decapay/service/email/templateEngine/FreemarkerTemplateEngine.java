package com.decagon.decapay.service.email.templateEngine;

import com.decagon.decapay.exception.TemplateEngineException;
import freemarker.template.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

import static com.decagon.decapay.constants.EmailConstants.TEMPLATE_FILE_EXT;
import static com.decagon.decapay.constants.EmailConstants.TEMPLATE_PATH;
import static com.decagon.decapay.constants.ResponseMessageConstants.UNABLE_TO_PROCESS_TEMPLATE;

@Service
@RequiredArgsConstructor
public class FreemarkerTemplateEngine implements TemplateEngine {
    private final Configuration freemarkerConfig;

    @Override
    public String processTemplateIntoString(String templateName, Map<String, String> templateTokens) {
        try {
            freemarkerConfig.setClassForTemplateLoading(this.getClass(), TEMPLATE_PATH);
            return FreeMarkerTemplateUtils
                    .processTemplateIntoString(freemarkerConfig.getTemplate(templateName+TEMPLATE_FILE_EXT), templateTokens);
        } catch (Exception e) {
            throw new TemplateEngineException(UNABLE_TO_PROCESS_TEMPLATE, e);
        }
    }

}