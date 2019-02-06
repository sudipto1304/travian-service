package com.travian.provider.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class AccountInfoResponse implements Serializable{
	
	private UserInfo user;
	
	private Map<String, String> cookies;
	private List<Village> villages;
	private int pendingAdventure;
	private int heroHealth;
	private int gold;
	private int silver;
	private String alliance;
	private String heroStatus;

}
