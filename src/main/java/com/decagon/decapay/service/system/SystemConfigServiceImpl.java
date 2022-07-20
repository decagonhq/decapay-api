package com.decagon.decapay.service.system;

import com.decagon.decapay.config.email.EmailConfig;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.model.systemConfig.SystemConfig;
import com.decagon.decapay.payloads.response.system.ConfigMetadata;
import com.decagon.decapay.payloads.response.system.SystemConfigDto;
import com.decagon.decapay.repositories.system.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.decagon.decapay.constants.EmailConstants.DEFAULT_CREATE_USER_EMAIL_SUBJ;
import static com.decagon.decapay.constants.EmailConstants.EMAIL_SUBJ_CREATE_USER_EMAIL;
import static com.decagon.decapay.constants.ResponseMessageConstants.CONFIG_VALUE_NOT_FOUND;
import static com.decagon.decapay.constants.SchemaConstants.*;

@RequiredArgsConstructor
@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    private final SystemConfigRepository repository;
    private final Environment env;


    @Override
    public String findConfigValueByKey(String key) {
        return this.repository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException(CONFIG_VALUE_NOT_FOUND));
    }

    @Override
    public void createDefaultConfigs() {
        Collection<SystemConfig> systemConfigs = this.repository.findAll();
        Map<String, SystemConfig> mapByKey = this.mapConfigsByKey(systemConfigs);
        SystemConfig configuration = null;

        //company name
        configuration = mapByKey.get(COMPANY_NAME);
        if (configuration == null) {
            configuration = new SystemConfig();
        }
        configuration.setConfigKey(COMPANY_NAME);
        configuration.setConfigName("Company Name");
        configuration.setValue(DEFAULT_COMPANY_NAME);
        configuration.setConfigGroup(GENERAL);
        configuration.setSortOrder(2);
        this.create(configuration);


        //app name
        if (!mapByKey.containsKey(APP_NAME)) {
            configuration = new SystemConfig();
            configuration.setConfigKey(APP_NAME);
            configuration.setConfigName("Application Name");
            configuration.setValue(DEFAULT_APP_NAME);
            configuration.setConfigGroup(GENERAL);
            configuration.setSortOrder(2);
            this.create(configuration);
        }

        //email config
        if (!mapByKey.containsKey(EMAIL_CONFIG)) {
            EmailConfig emailConfig = new EmailConfig();
            emailConfig.setHost(env.getProperty("spring.mail.host"));
            emailConfig.setProtocol(env.getProperty("spring.mail.properties.mail.transport.protocol"));
            emailConfig.setPort(env.getProperty("spring.mail.port"));
            emailConfig.setSmtpAuth(Objects.equals(env.getProperty("spring.mail.properties.mail.smtp.auth"), "true"));
            emailConfig.setStarttls(Objects.equals(env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"), "true"));
            emailConfig.setUsername(env.getProperty("spring.mail.username"));
            emailConfig.setPassword(env.getProperty("spring.mail.password"));

            configuration = new SystemConfig();
            configuration.setConfigKey(EMAIL_CONFIG);
            configuration.setConfigName("Email Configuration");
            configuration.setDescription("Set up default email configuration");
            configuration.setValue(emailConfig.toJSONString());
            configuration.setConfigGroup(EMAIL);

            this.create(configuration);
        }

        //email subjects

        if (!mapByKey.containsKey(SUPPORT_EMAIL)) {
            configuration = new SystemConfig();
            configuration.setConfigKey(SUPPORT_EMAIL);
            configuration.setConfigName("Support email");
            configuration.setValue(DEFAULT_SUPPORT_EMAIL);
            configuration.setDescription("Support email use for support and other administrative purposes");
            configuration.setConfigGroup(EMAIL);
            configuration.setSortOrder(1);
            this.create(configuration);
        }

        if (!mapByKey.containsKey(EMAIL_SUBJ_CREATE_USER_EMAIL)) {
            configuration = new SystemConfig();
            configuration.setConfigKey(EMAIL_SUBJ_CREATE_USER_EMAIL);
            configuration.setConfigName("Create User Email Subject");
            configuration.setValue(DEFAULT_CREATE_USER_EMAIL_SUBJ);
            configuration.setConfigGroup(EMAIL);
            configuration.setDescription("Set up default email subject for create user email");
            configuration.setSortOrder(3);
            this.create(configuration);
        }


    }

    private void create(SystemConfig configuration) {
        this.repository.save(configuration);
    }

    @Override
    public Collection<SystemConfigDto> fetchSystemConfigs(String group) {
        Collection<SystemConfig> systemConfigurations = this.repository.findByConfigGroupOrderBySortOrderAsc(group);
        return this.convertModelsToDto(systemConfigurations);
    }

    @Override
    public void updateSystemConfigs(String group, Map<String, String> dto) {
        Collection<SystemConfig> systemConfigurations = this.repository.findByConfigGroupOrderBySortOrderAsc(group);
        Map<String, SystemConfig> mapByKey = this.mapConfigsByKey(systemConfigurations);
        dto.forEach((key, value) -> {
            if (mapByKey.containsKey(key)) {
                mapByKey.get(key).setValue(value);
            }
        });
    }

    private Map<String, SystemConfig> mapConfigsByKey(Collection<SystemConfig> systemConfigurations) {
        Map<String, SystemConfig> mapByKey = null;
        if (CollectionUtils.isNotEmpty(systemConfigurations)) {
            mapByKey = new HashMap<>();
            for (SystemConfig config : systemConfigurations) {
                mapByKey.put(config.getConfigKey(), config);
            }
        }
        return mapByKey == null ? Collections.emptyMap() : mapByKey;
    }

    private Collection<SystemConfigDto> convertModelsToDto(Collection<SystemConfig> systemConfigs) {

        Collection<SystemConfigDto> dtos = new ArrayList<>();

        for (SystemConfig systemConfig : systemConfigs) {
            SystemConfigDto dto = new SystemConfigDto();
            dto.setDescription(systemConfig.getDescription());
            dto.setKey(systemConfig.getConfigKey());
            dto.setName(systemConfig.getConfigName());
            dto.setValue(systemConfig.getValue());
            dto.setType(systemConfig.getSystemConfigType().name());
            ConfigMetadata metadata = new ConfigMetadata();
            dto.setMetadata(metadata);
            dtos.add(dto);
        }
        return dtos;
    }
}
