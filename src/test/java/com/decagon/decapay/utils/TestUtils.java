package com.decagon.decapay.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

	public static String asJsonString(final Object obj) {
		try {
			ObjectMapper myObjectMapper = new ObjectMapper();
			myObjectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String resp=myObjectMapper.writeValueAsString(obj);
			System.out.println("json-string/"+resp);
			return resp;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
