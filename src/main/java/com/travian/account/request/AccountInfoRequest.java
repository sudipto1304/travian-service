package com.travian.account.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountInfoRequest {
	private String userId;
	private String password;
	private String serverUri;
	

}
