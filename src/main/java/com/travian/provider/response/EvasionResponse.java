package com.travian.provider.response;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EvasionResponse implements Serializable{
	
	private String destinationVillage;
	private String t1;
	private String t2;
	private String t3;
	private String t4;
	private String t5;
	private String t6;
	private String t7;
	private String t8;
	private String t9;
	private String t10;

}
