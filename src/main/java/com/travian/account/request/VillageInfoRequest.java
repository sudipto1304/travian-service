package com.travian.account.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VillageInfoRequest implements Serializable{
	
	private Map<String, String> cookies;
	private List<String> link;
	private String host;
	private String userId;

}
