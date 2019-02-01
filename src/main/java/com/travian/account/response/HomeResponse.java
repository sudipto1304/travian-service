package com.travian.account.response;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HomeResponse {
	
	private int statusCode;
	private HttpStatus status;
	private String login;
	private String w;
	private String sVal;
	private Map<String, String> cookies;

}
 