package com.decagon.decapay.service.system;

import com.decagon.decapay.payloads.response.system.SystemConfigDto;

import java.util.Collection;
import java.util.Map;

public interface SystemConfigService {
    String findConfigValueByKey(String key);

    void createDefaultConfigs();

    Collection<SystemConfigDto> fetchSystemConfigs(String group);

    void updateSystemConfigs(String group, Map<String,String> dto);
}
