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
public class Resource implements Serializable{
	
	private int warehouseCapacity;
	private int granaryCapacity;
	private int wood;
	private int clay;
	private int iron;
	private int crop;
	private int woodProduction;
	private int clayProduction;
	private int ironProduction;
	private int cropProduction;
	private int cropUsage;
	private List<Fields> fields;

}
