package com.travian.provider.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.travian.provider.request.CelebrationRequest;
import com.travian.provider.request.DeleteTradeRouteRequest;
import com.travian.provider.request.HttpRequest;
import com.travian.provider.request.TradeRouteRequest;
import com.travian.provider.request.TroopEvasionRequest;
import com.travian.provider.request.VillageInfoRequest;
import com.travian.provider.response.EvasionResponse;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.Status;
import com.travian.provider.response.Village;
import com.travian.provider.util.VillageUtil;

@Service
public class VillageService {
	private static final Logger Log = LoggerFactory.getLogger(VillageService.class);
	
	
	@Autowired
	private HTTPRequestService httpService;
	
	public List<Village> getVillagesInfo(VillageInfoRequest request){
		List<Village> response = request.getLink().stream().map(e->getVillageInfo(e, request.getCookies(), request.getHost())).collect(Collectors.toList());
		return response;
	}

	private Village getVillageInfo(String link, Map<String, String> cookies, String host){
		try {
			Village village = new Village();
			HttpRequest resourceRequest  = new HttpRequest();
			resourceRequest.setCookies(cookies);
			resourceRequest.setHost(host);
			resourceRequest.setHttpMethod(HttpMethod.GET);
			resourceRequest.setPath("/dorf1.php"+link);
			HttpResponse resourceResponse = httpService.get(resourceRequest);
			village.setLink(link);
			village.setVillageId(link.substring(link.indexOf("=")+1, link.length()-1));
			VillageUtil.parseDorf1(resourceResponse, village);
			
			HttpRequest buildingRequest  = new HttpRequest();
			buildingRequest.setCookies(cookies);
			buildingRequest.setHost(host);
			buildingRequest.setHttpMethod(HttpMethod.GET);
			buildingRequest.setPath("/dorf2.php"+link);
			HttpResponse buildingResponse = httpService.get(buildingRequest);
			VillageUtil.parseDorf2(buildingResponse, village);
			return village;
		} catch (Exception e) {
			Log.error("", e);
		}
		return null;
	}
	
	
	
	
	
	public Status initiateCelebration(CelebrationRequest request) throws IOException {
		HttpRequest townHallRequest = new HttpRequest();
		townHallRequest.setCookies(request.getCookies());
		townHallRequest.setHost(request.getHost());
		townHallRequest.setPath("/build.php?newdid="+request.getVillageId()+"&id="+request.getThId());
		townHallRequest.setHttpMethod(HttpMethod.GET);
		HttpResponse townHallResponse = httpService.get(townHallRequest);
		Map<String, Object> thResponse = VillageUtil.parseTHResponse(townHallResponse);
		if((boolean)thResponse.get("isCelebrationPossible")) {
			HttpRequest celebrationRequest = new HttpRequest();
			celebrationRequest.setCookies(request.getCookies());
			celebrationRequest.setHost(request.getHost());
			celebrationRequest.setPath("/"+(String)thResponse.get("link"));
			celebrationRequest.setHttpMethod(HttpMethod.GET);
			HttpResponse celebrationResponse = httpService.get(celebrationRequest);
			return new Status(VillageUtil.getFinishTime(celebrationResponse), 200);
		}else {
			String finishTime = VillageUtil.getFinishTime(townHallResponse);
			if(StringUtils.isEmpty(finishTime)) {
				return new Status("NOT.ENOUGH.RESOURCE", 400);
			}else {
				return new Status("CELEBRATION.ONGOING", 400);
			}
		}
		
	}
	
	
}
