package com.decagon.decapay.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.decagon.decapay.constants.SchemaConstants.*;

@Component
public class DBCleanerUtil {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void cleanDb(){
        clearDb(jdbcTemplate);
    }

    public static void clearDb(JdbcTemplate jdbcTemplate){

        List<String> tables = new ArrayList<>();
        tables.add(TABLE_BUDGET_CATEGORY);
        tables.add(TABLE_BUDGET_LINE_ITEM);
        tables.add(TABLE_BUDGET);
        tables.add(TABLE_EXPENSES);
        tables.add(TABLE_PASSWORD_RESET);
        tables.add(TABLE_USER);


        String[] excludes={};
        List<String> excludesList = Arrays.asList(excludes);
        if (CollectionUtils.isNotEmpty(excludesList)) {
            tables.removeAll(excludesList);
        }

        String[] tablesArr = new String[tables.size()];
        tablesArr = tables.toArray(tablesArr);

        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                tablesArr);
    }

    public static void clearTables(JdbcTemplate jdbcTemplate, String... tables) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, tables);
    }
}
