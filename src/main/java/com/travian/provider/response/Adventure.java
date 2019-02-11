package com.travian.provider.response;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Adventure implements Serializable{
	
	private String link;
	private String remainingTime;
	private String type;
	private String place;
	private int x;
	private int y;
	private String timeRequired;

}
