package com.travian.provider.request;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class GameWorld implements Serializable{
	
	private String host;
	private String path;
	private String userId;
	private Map<String, String> cookies;

}
