package com.decagon.decapay.utils.extensions;

import com.decagon.decapay.utils.DBCleanerUtil;
import org.junit.jupiter.api.extension.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DBCleanerExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {


    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
     
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        if(!extensionContext.getTags().contains("parameterized")){
            DBCleanerUtil.clearDb(SpringExtension.getApplicationContext(extensionContext).getBean(JdbcTemplate.class));
        }
    }

}
