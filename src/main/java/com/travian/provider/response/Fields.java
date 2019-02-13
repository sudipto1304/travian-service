package com.travian.provider.response;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Fields implements Serializable, Comparable{
	
	private String type;
	private int level;
	private int nextLevelWood;
	private int nextLevelClay;
	private int nextLevelIron;
	private int nextLevelCrop;
	private int id;
	private String link;
	private boolean isUpgradable;
	
	@Override
	public int compareTo(Object o) {
		return Integer.valueOf(this.getId()).compareTo(Integer.valueOf(((Building)o).getId()));
	}
	

}
