package com.travian.provider.request;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class TroopTrainRequest  extends GameWorld implements Serializable{
	
	private String villageId;
	private String buildingId;
	private String troopType;
	

}
