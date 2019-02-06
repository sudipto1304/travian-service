package com.travian.provider.request;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountInfoRequest implements Serializable{
	private String userId;
	private String password;
	private String serverUri;
	

}
