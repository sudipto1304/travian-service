package com.travian.provider.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.travian.provider.request.AccountInfoRequest;
import com.travian.provider.request.GameWorld;
import com.travian.provider.request.HttpRequest;
import com.travian.provider.request.InitiateAdventureRequest;
import com.travian.provider.response.AccountInfoResponse;
import com.travian.provider.response.Adventure;
import com.travian.provider.response.HomeResponse;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.Status;
import com.travian.provider.util.AccountUtil;

@Service
public class AccountService {

	private static final Logger Log = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private HTTPRequestService httpService;

	public AccountInfoResponse getAccountInfo(AccountInfoRequest request) throws IOException {
		HomeResponse home = getHomeResponse(request);
		HttpRequest loginRequest = new HttpRequest();
		loginRequest.setCookies(home.getCookies());
		loginRequest.setHttpMethod(HttpMethod.POST);
		loginRequest.setHost(request.getHost());
		loginRequest.setPath("/dorf1.php");
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("s1", home.getSVal());
		postData.put("w", home.getW());
		postData.put("login", home.getLogin());
		postData.put("name", request.getUserId());
		postData.put("password", request.getPassword());
		loginRequest.setData(postData);
		HttpResponse loginResponse = httpService.post(loginRequest);
		loginResponse.getCookies().putAll(home.getCookies());
		return AccountUtil.parseAccountResponse(loginResponse);
	}

	public AccountInfoResponse getAccountInfoWL(GameWorld request) throws IOException {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.GET);
		httpRequest.setHost(request.getHost());
		httpRequest.setPath("/dorf1.php");
		httpRequest.setCookies(request.getCookies());
		HttpResponse homeResponse = httpService.get(httpRequest);
		AccountInfoResponse response =  AccountUtil.parseAccountResponse(homeResponse);
		response.setCookies(request.getCookies());
		return response;
	}

	private HomeResponse getHomeResponse(GameWorld request) throws IOException {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.GET);
		httpRequest.setHost(request.getHost());
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
	
	public List<Adventure> getAdventureList(GameWorld request) throws IOException{
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.GET);
		httpRequest.setHost(request.getHost());
		httpRequest.setPath("/hero.php?t=3");
		httpRequest.setCookies(request.getCookies());
		HttpResponse adventure = httpService.get(httpRequest);
		return AccountUtil.parseAdventure(adventure);
	}
	
	public Status initiateAdventure(InitiateAdventureRequest request) throws IOException {
		Status status = null;
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setHost(request.getHost());
		httpRequest.setPath(request.getPath());
		httpRequest.setCookies(request.getCookies());
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("from", request.getFrom());
		postData.put("send", request.getSend());
		postData.put("kid", request.getKid());
		postData.put("a", request.getA());
		httpRequest.setData(postData);
		HttpResponse adventureResponse = httpService.post(httpRequest);
		if(adventureResponse.getHttpStatusCode()==200) {
			status = new Status("SUCCESS", 200);
		}
		return status;
	}

	

}
