package com.decagon.decapay.utils;


import com.decagon.decapay.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class ResourceHelper {
	public static <T>  T validateResourceExists (Optional<T> resource, String message) {
		if (resource.isEmpty())
			throw new ResourceNotFoundException("User does not exist");
		return resource.get();
	}
}

