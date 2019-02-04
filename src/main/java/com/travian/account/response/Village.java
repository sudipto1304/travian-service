package com.travian.account.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class Village implements Serializable{
	
	private String link;
	private String villageName;
	private int x;
	private int y;
	private int loyalty;
	private boolean isActive=false;
	private Resource resource;
	

}
