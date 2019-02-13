package com.travian.provider.request;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpgradeRequest extends GameWorld implements Serializable{
	
	private String id;
	private String villageId;
	

}
