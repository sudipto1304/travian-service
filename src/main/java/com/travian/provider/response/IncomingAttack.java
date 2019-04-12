package com.travian.provider.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
@JsonInclude(Include.NON_NULL)
public class IncomingAttack implements Serializable{
	
	private String attackText;
	private int attackCount;
	private Long duration;
	private String link;

}
