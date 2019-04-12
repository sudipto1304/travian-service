package com.travian.provider.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class Village implements Serializable{
	
	private String link;
	private String villageId;
	private String villageName;
	private int x;
	private int ongoingConstruction;
	private int y;
	private int loyalty;
	private boolean isActive=false;
	private Resource resource;
	private List<Building> buildings;
	private List<VillageTroop> villageTroops;
	private boolean isTownHallPresent;
	private int thId;
	private IncomingAttack incomingAttack;

}
