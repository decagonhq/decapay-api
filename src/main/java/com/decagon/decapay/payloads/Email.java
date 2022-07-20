package com.decagon.decapay.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Email implements Serializable {
	private String from;
	private String fromEmail;
	private String to;
	private String subject;
	private String templateName;
	private Map<String,String> templateTokens = new HashMap<>();
	private String body;
	private FileAttachement attachement;

	@Data
	@AllArgsConstructor
	public static class FileAttachement {
		private String fileName;
		private InputStream inputStream;
	}
}
