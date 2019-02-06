package com.travian.provider.response;

import java.io.Serializable;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class HomeResponse implements Serializable{
	
	private int statusCode;
	private HttpStatus status;
	private String login;
	private String w;
	private String sVal;
	private Map<String, String> cookies;

}
 