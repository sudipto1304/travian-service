package com.travian.account.response;

import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HttpResponse {
	
	private String body;
	private int httpStatusCode;
	private String httpStatus;
	private Map<String, String> cookies;
	

}
