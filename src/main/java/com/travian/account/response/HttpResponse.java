package com.travian.account.response;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HttpResponse {
	
	private String body;
	private int httpStatusCode;
	private String httpStatus;
	private List<String> cookies;
	

}
