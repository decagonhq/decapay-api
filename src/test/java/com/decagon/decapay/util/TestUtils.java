package com.decagon.decapay.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;


public class TestUtils {
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper myObjectMapper = new ObjectMapper();
            myObjectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            String resp=myObjectMapper.writeValueAsString(obj);
            System.out.println("json-string/"+resp);
            return resp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T objectFromResponseStr(String response, String jsonPathStr) {
        return JsonPath.parse(response).read(jsonPathStr);
    }

}
