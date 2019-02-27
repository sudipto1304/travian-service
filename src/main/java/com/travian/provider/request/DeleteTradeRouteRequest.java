package com.travian.provider.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeleteTradeRouteRequest extends GameWorld implements Serializable{
	private String villageId;

}
