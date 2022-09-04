package com.decagon.decapay.init.data;


import com.decagon.decapay.constants.SchemaConstants;
import com.decagon.decapay.service.reference.init.InitializationDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Profile("!test")
@Slf4j
public class InitializationLoader {
    @Autowired
    private InitializationDatabase initializationDatabase;

    @PostConstruct
    public void init() {

        try {

            //All default data to be created
            log.info(String.format("%s : Decapay database is empty, populate it....", "decapay"));
            initializationDatabase.populate(SchemaConstants.DEFAULT_APP_NAME);

        } catch (Exception e) {
            log.error("Error in the init method", e);
        }

    }


}
