package com.travian.provider.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.travian.provider.exception.TaskExecutionException;
import com.travian.provider.request.HttpRequest;
import com.travian.provider.request.UpgradeRequest;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.Status;
import com.travian.provider.util.UpgradeUtils;

@Service
public class UpgradeService {
	
	private static final Logger Log = LoggerFactory.getLogger(UpgradeService.class);
	
	@Autowired
	private HTTPRequestService httpService;
	
	
	public Status upgradeBuilding(UpgradeRequest request) throws IOException {
		try {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.setCookies(request.getCookies());
			httpRequest.setHost(request.getHost());
			httpRequest.setHttpMethod(HttpMethod.GET);
			httpRequest.setPath("/build.php?newdid="+request.getVillageId()+"&id="+request.getId());
			HttpResponse httpResponse = httpService.get(httpRequest);
			String upgradeLink = UpgradeUtils.getUpgradePath(httpResponse);
			HttpRequest upgradeRequest = new HttpRequest();
			upgradeRequest.setCookies(request.getCookies());
			upgradeRequest.setHost(request.getHost());
			upgradeRequest.setHttpMethod(HttpMethod.GET);
			upgradeRequest.setPath("/"+upgradeLink);
			HttpResponse upgradeResponse = httpService.get(upgradeRequest);
			return new Status(String.valueOf(UpgradeUtils.getOnGoingUpgradeCount(upgradeResponse)), 200);
		} catch (Exception e) {
			throw new TaskExecutionException();
		}
		
	}

}
