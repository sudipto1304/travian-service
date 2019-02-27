package com.travian.provider.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class TroopTrainResponse implements Serializable{
	
	private int count;
	private String timeRequired;
	private String villageId;

}
