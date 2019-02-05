package com.travian.account.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class Building implements Serializable{
	
	private String buildingName;
	private int buildingLevel;
	private int id;
	private String link;
	private int nextLevelWood;
	private int nextLevelClay;
	private int nextLevelIron;
	private int nextLevelCrop;
	private boolean isUpgradable=false;
	private boolean isUpgrading=false;

}
