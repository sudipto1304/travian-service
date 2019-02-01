package com.travian.account.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import com.travian.account.response.UserInfo;

@Service
public class AccountInfoService {
	
	private static final Logger Log = LoggerFactory.getLogger(AccountInfoService.class);
	
	@Autowired
	private HTTPRequestService httpService;
	
	
	public AccountInfoResponse getAccountInfo(AccountInfoRequest request) throws IOException {
		
		HomeResponse home = getHomeResponse(request);
		HttpRequest loginRequest = new HttpRequest();
		loginRequest.setCookies(home.getCookies());
		loginRequest.setHttpMethod(HttpMethod.POST);
		loginRequest.setHost(request.getServerUri());
		loginRequest.setPath("/dorf1.php");
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("s1", home.getSVal());
		postData.put("w", home.getW());
		postData.put("login", home.getLogin());
		postData.put("name", request.getUserId());
		postData.put("password", request.getPassword());
		loginRequest.setData(postData);
		HttpResponse loginResponse = httpService.post(loginRequest);
		return parseAccountResponse(loginResponse);
	}
	
	private HomeResponse getHomeResponse(AccountInfoRequest request) throws IOException {
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
	
	private AccountInfoResponse parseAccountResponse(HttpResponse loginResponse) {
		AccountInfoResponse response = new AccountInfoResponse();
		UserInfo user = new UserInfo();
		Document doc = Jsoup.parse(loginResponse.getBody());
		Elements userInfoElement = doc.select("div.playerName > a");
		user.setTribe(userInfoElement.get(0).getElementsByClass("nation").attr("title"));
		user.setLink("/"+userInfoElement.get(1).attr("href"));
		user.setUserName(userInfoElement.get(1).text());
		response.setUser(user);
		return response;
	}

}
