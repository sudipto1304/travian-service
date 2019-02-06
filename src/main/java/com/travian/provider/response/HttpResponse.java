package com.travian.provider.response;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class HttpResponse implements Serializable{
	
	private String body;
	private int httpStatusCode;
	private String httpStatus;
	private Map<String, String> cookies;
	

}
