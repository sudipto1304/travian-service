package com.travian.account.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Fields {
	
	private String type;
	private int level;
	private int nextLevelWood;
	private int nextLevelClay;
	private int nextLevelIron;
	private int nextLevelCrop;
	private int id;
	private String link;
	private boolean isUpgradable;
	

}
