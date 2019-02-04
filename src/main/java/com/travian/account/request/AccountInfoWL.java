package com.travian.account.request;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountInfoWL implements Serializable{
	
	private String userId;
	private Map<String, String> cookies;
	private String serverUri;
	private String path;

}
