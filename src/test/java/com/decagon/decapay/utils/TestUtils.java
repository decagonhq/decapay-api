package com.decagon.decapay.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class TestUtils {

	public static String asJsonString(final Object obj) {
		try {
			ObjectMapper myObjectMapper = new ObjectMapper().findAndRegisterModules();
			myObjectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			return myObjectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static short getNonCurrentMonth(int currMonth){
		if(currMonth==12){
			return 1;
		}else if(currMonth==0){
			return 2;
		}
		return (short)currMonth;
	}

	public static <T> T objectFromResponseStr(String response, String jsonPathStr) {
		return JsonPath.parse(response).read(jsonPathStr);
	}

}
