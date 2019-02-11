package com.travian.provider.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.travian.provider.request.HttpRequest;
import com.travian.provider.request.TradeRouteRequest;
import com.travian.provider.request.VillageInfoRequest;
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
			VillageUtil.parseResource(resourceResponse, village);
			VillageUtil.parseCommonAttributes(resourceResponse, village);
			
			HttpRequest buildingRequest  = new HttpRequest();
			buildingRequest.setCookies(cookies);
			buildingRequest.setHost(host);
			buildingRequest.setHttpMethod(HttpMethod.GET);
			buildingRequest.setPath("/dorf2.php"+link);
			HttpResponse buildingResponse = httpService.get(buildingRequest);
			VillageUtil.parseBuildingResponse(buildingResponse, village);
			VillageUtil.parseVillageTroops(resourceResponse, village);
			return village;
		} catch (Exception e) {
			Log.error("", e);
		}
		return null;
	}
	
	public Status createTradeRoutes(final TradeRouteRequest request) throws IOException {
		HttpRequest marketPlaceRequest = new HttpRequest();
		marketPlaceRequest.setCookies(request.getCookies());
		marketPlaceRequest.setHost(request.getHost());
		marketPlaceRequest.setPath("/dorf2.php?newdid="+request.getSourceVillage()+"&");
		marketPlaceRequest.setHttpMethod(HttpMethod.GET);
		HttpResponse marketPlaceResponse = httpService.get(marketPlaceRequest);
		if(Log.isDebugEnabled())
			Log.debug("getting merket place request");
		HttpRequest tradeRouteRequest = new HttpRequest();
		tradeRouteRequest.setCookies(request.getCookies());
		tradeRouteRequest.setHost(request.getHost());
		tradeRouteRequest.setHttpMethod(HttpMethod.GET);
		tradeRouteRequest.setPath("/build.php?t=0&gid=17&option=1&show-destination=all");
		HttpResponse tradeRouteResponse = httpService.get(tradeRouteRequest);
		VillageUtil.parseMarketPlaceTradeRoutes(tradeRouteResponse, request);
		request.getTime().forEach(e->{
			HttpRequest tradeRouteRq = new HttpRequest();
			tradeRouteRq.setCookies(request.getCookies());
			tradeRouteRq.setHost(request.getHost());
			tradeRouteRq.setHttpMethod(HttpMethod.POST);
			tradeRouteRq.setPath("/build.php");
			Map<String, String> postData = new HashMap<String, String>();
			postData.put("did_dest", request.getDestinationVillage());
			postData.put("r1", request.getWood());
			postData.put("r2", request.getClay());
			postData.put("r3", request.getIron());
			postData.put("r4", request.getCrop());
			postData.put("userHour", e);
			postData.put("repeat", "1");
			postData.put("gid", request.getGid());
			postData.put("a", request.getA());
			postData.put("t", request.getT());
			postData.put("trid", request.getTrid());
			postData.put("option", request.getOption());
			if(Log.isInfoEnabled())
				Log.info("creating trade routes for "+postData+" from village ::"+request.getSourceVillage()+"::to village::"+request.getDestinationVillage());
			tradeRouteRq.setData(postData);
			try {
				HttpResponse tradeRouteRes= httpService.post(tradeRouteRq);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		return new Status("SUCCESS", 200);	
	}
}
