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
			postData.put("hour", e);
			postData.put("minute", "0");
			postData.put("trade_route_mode", "send");
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
	
	
	public Status deleteAllTradeRoutes(DeleteTradeRouteRequest request) throws IOException {
		List<String> routes = getAllTradeRoutes(request);
		routes.forEach(e->{
			HttpRequest delteRoutes = new HttpRequest();
			delteRoutes.setCookies(request.getCookies());
			delteRoutes.setHost(request.getHost());
			delteRoutes.setPath(e);
			delteRoutes.setHttpMethod(HttpMethod.GET);
			try {
				HttpResponse delteRoutesResponse = httpService.get(delteRoutes);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		return new Status("SUCCESS", 200);
	}
	
	public List<String> getAllTradeRoutes(DeleteTradeRouteRequest request) throws IOException{
		HttpRequest marketPlaceRequest = new HttpRequest();
		marketPlaceRequest.setCookies(request.getCookies());
		marketPlaceRequest.setHost(request.getHost());
		marketPlaceRequest.setPath("/build.php?newdid="+request.getVillageId()+"&gid=17");
		marketPlaceRequest.setHttpMethod(HttpMethod.GET);
		HttpResponse marketPlaceResponse = httpService.get(marketPlaceRequest);
		return VillageUtil.getTradeRoutes(marketPlaceResponse);
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
	
	public EvasionResponse initiateTroopEvasion(TroopEvasionRequest request) throws IOException {
		HttpRequest rallyPointRequest = new HttpRequest();
		rallyPointRequest.setCookies(request.getCookies());
		rallyPointRequest.setHost(request.getHost());
		rallyPointRequest.setPath("/build.php?newdid="+request.getVillageId()+"&tt=2&id=39");
		rallyPointRequest.setHttpMethod(HttpMethod.GET);
		HttpResponse rallyPointResponse = httpService.get(rallyPointRequest);
		Map<String, String> data = VillageUtil.parseSendTroopResponse(rallyPointResponse);
		data.put("s1", "ok");
		data.put("c", "2");
		data.put("dname", request.getDestinationName());
		data.put("x", "");
		data.put("y", "");
		HttpRequest evasionRequest1 = new HttpRequest();
		evasionRequest1.setCookies(request.getCookies());
		evasionRequest1.setHost(request.getHost());
		evasionRequest1.setPath("/build.php?newdid="+request.getVillageId()+"&tt=2&id=39");
		evasionRequest1.setHttpMethod(HttpMethod.POST);
		evasionRequest1.setData(data);
		Map<String, String> finalData = VillageUtil.parseSendTroopConfirmResponse(httpService.post(evasionRequest1));
		HttpRequest evasionConfirmRequest = new HttpRequest();
		evasionConfirmRequest.setCookies(request.getCookies());
		evasionConfirmRequest.setHost(request.getHost());
		evasionConfirmRequest.setPath("/build.php?newdid="+request.getVillageId()+"&tt=2&id=39");
		evasionConfirmRequest.setHttpMethod(HttpMethod.POST);
		evasionConfirmRequest.setData(finalData);
		httpService.post(evasionConfirmRequest);
		EvasionResponse response = new EvasionResponse();
		response.setDestinationVillage(finalData.get("dname"));
		response.setT1(finalData.get("t1"));
		response.setT2(finalData.get("t2"));
		response.setT3(finalData.get("t3"));
		response.setT4(finalData.get("t4"));
		response.setT5(finalData.get("t5"));
		response.setT6(finalData.get("t6"));
		response.setT7(finalData.get("t7"));
		response.setT8(finalData.get("t8"));
		response.setT9(finalData.get("t9"));
		response.setT10(finalData.get("t10"));
		return response;
	}
	
	public Status resolveEvasion(TroopEvasionRequest request) throws IOException {
		HttpRequest rallyPointRequest = new HttpRequest();
		rallyPointRequest.setCookies(request.getCookies());
		rallyPointRequest.setHost(request.getHost());
		rallyPointRequest.setPath("/build.php?newdid="+request.getVillageId()+"&tt=1&id=39");
		rallyPointRequest.setHttpMethod(HttpMethod.GET);
		HttpResponse rallyPointResponse = httpService.get(rallyPointRequest);
		String link = VillageUtil.getCrossLink(rallyPointResponse);
		if(StringUtils.isNotEmpty(link)) {
			HttpRequest evasionCancelRequest = new HttpRequest();
			evasionCancelRequest.setCookies(request.getCookies());
			evasionCancelRequest.setHost(request.getHost());
			evasionCancelRequest.setPath("/build.php"+link);
			evasionCancelRequest.setHttpMethod(HttpMethod.GET);
			HttpResponse evasionCancelResponse = httpService.get(evasionCancelRequest);
			return new Status("SUCCESS", 200);
		}else {
			return new Status("EVASION.CANCEL.TIMEOUT", 400);
		}
	}
}
