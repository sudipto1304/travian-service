package com.travian.account.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.travian.account.request.AccountInfoRequest;
import com.travian.account.request.HttpRequest;
import com.travian.account.response.AccountInfoResponse;
import com.travian.account.response.HomeResponse;
import com.travian.account.response.HttpResponse;

@Service
public class AccountInfoService {
	
	private static final Logger Log = LoggerFactory.getLogger(AccountInfoService.class);
	
	@Autowired
	private HTTPRequestService httpService;
	
	
	public AccountInfoResponse getAccountInfo(AccountInfoRequest request) throws IOException {
		AccountInfoResponse response = new AccountInfoResponse();
		HomeResponse home = getHome(request);
		HttpRequest loginRequest = new HttpRequest();
		loginRequest.setCookie(home.getCookies());
		loginRequest.setHttpMethod(HttpMethod.POST);
		loginRequest.setHost(request.getServerUri());
		loginRequest.setPath("dorf1.php");
		Map<String, Object> postData = new HashMap<String, Object>();
		postData.put("s1", home.getSVal());
		postData.put("w", home.getW());
		postData.put("login", home.getLogin());
		postData.put("name", request.getUserId());
		postData.put("password", request.getPassword());
		loginRequest.setData(postData);
		HttpResponse loginResponse = httpService.post(loginRequest);
		return response;
	}
	
	private HomeResponse getHome(AccountInfoRequest request) throws IOException {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.GET);
		httpRequest.setHost(request.getServerUri());
		HttpResponse response = httpService.get(httpRequest);
		HomeResponse homeResponse = new HomeResponse();
		homeResponse.setCookies(response.getCookies());
		homeResponse.setStatus(HttpStatus.valueOf(response.getHttpStatus()));
		homeResponse.setStatusCode(response.getHttpStatusCode());
		Document doc = Jsoup.parse(response.getBody());
		homeResponse.setLogin(doc.select("input[name=login").attr("value"));
		homeResponse.setSVal("Login");
		homeResponse.setW("1366:768");
		return homeResponse;
	}

}
