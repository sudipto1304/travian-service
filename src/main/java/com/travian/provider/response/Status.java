package com.travian.provider.response;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Status implements Serializable{
	
	private String status;
	private int statusCode;
	
	public Status() {
		
	}
	public Status(String status, int statusCode) {
		this.status = status;
		this.statusCode  = statusCode;
	}

}
