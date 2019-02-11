package com.travian.provider.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class TradeRouteRequest extends GameWorld implements Serializable{
	
	private String destinationVillage;
	private String sourceVillage;
	private String wood;
	private String clay;
	private String iron;
	private String crop;
	private List<String> time;
	private String numberOfDelivery;
	private String gid;
	private String a;
	private String t;
	private String trid;
	private String option;
	

}
