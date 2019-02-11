package com.travian.provider.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VillageInfoRequest extends GameWorld implements Serializable{
	
	private Map<String, String> cookies;
	private List<String> link;

}
