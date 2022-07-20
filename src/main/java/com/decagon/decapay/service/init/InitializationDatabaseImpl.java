package com.decagon.decapay.service.init;


import com.decagon.decapay.service.system.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class InitializationDatabaseImpl implements InitializationDatabase {

    private final SystemConfigService configService;

    private String name;

    @Transactional
    public void populate(String contextName) {
        this.name = contextName;
        createConfigurations();
    }

    private void createConfigurations() {
        log.info(String.format("%s : Creating default configurations ", name));
        this.configService.createDefaultConfigs();
    }

}
