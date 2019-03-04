package com.travian.provider.request;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CelebrationRequest extends GameWorld implements Serializable{
	private String villageId;
	private String thId;
	

}
