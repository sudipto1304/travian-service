package com.travian.account.request;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;

import lombok.Data;

@Data
public class HttpRequest {
	
	private String path;
	private String host;
	private HttpMethod httpMethod;
	private Map<String, Object> data;
	private List<String> cookie;

}
