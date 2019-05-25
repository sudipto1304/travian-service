package com.travian.provider.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.travian.provider.request.DeleteTradeRouteRequest;
import com.travian.provider.request.HttpRequest;
import com.travian.provider.request.TradeRouteRequest;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.Status;
import com.travian.provider.util.ResourceUtil;
import com.travian.provider.util.VillageUtil;

@Component
public class ResourceService {
	
	private static final Logger Log = LoggerFactory.getLogger(ResourceService.class);
	
	@Autowired
	private HTTPRequestService httpService;
	
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
			postData.put("every", "24");
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
	
	public Status transferResource(final TradeRouteRequest request) throws IOException, UnirestException, JSONException {
		HttpRequest marketPlaceRequest = new HttpRequest();
		marketPlaceRequest.setCookies(request.getCookies());
		Map<String, String> requestData = new HashMap<String, String>();
		marketPlaceRequest.setHost(request.getHost());
		marketPlaceRequest.setPath("/dorf2.php?newdid="+request.getSourceVillage()+"&");
		marketPlaceRequest.setHttpMethod(HttpMethod.GET);
		HttpResponse marketPlaceResponse = httpService.get(marketPlaceRequest);
		if(Log.isDebugEnabled())
			Log.debug("getting merket place request");
		HttpRequest transferRequest = new HttpRequest();
		transferRequest.setCookies(request.getCookies());
		transferRequest.setHost(request.getHost());
		transferRequest.setHttpMethod(HttpMethod.GET);
		transferRequest.setPath("/build.php?t=5&gid=17");
		HttpResponse transferResponse = httpService.get(transferRequest);
		Map<String, String> data = ResourceUtil.parseSendResource(transferResponse);
		int totalResource = Integer.valueOf(request.getClay())+Integer.valueOf(request.getCrop())+Integer.valueOf(request.getWood())+Integer.valueOf(request.getIron());
		if(totalResource>Integer.valueOf(data.get("capacity"))) {
			return new Status("NOT.ENOUGH.MERCHANTS", 400);
		}
		StringBuilder bodyData = new StringBuilder();
		bodyData.append("cmd=prepareMarketplace&").append("r1="+request.getWood()+"&")
		.append("r2="+request.getClay()+"&")
		.append("r3="+request.getIron()+"&")
		.append("r4="+request.getCrop()+"&")
		.append("dname="+request.getDestinationVillage()+"&")
		.append("id="+data.get("id")+"&")
		.append("t="+data.get("t")+"&")
		.append("X2=1"+"&")
		.append("ajaxToken="+data.get("ajaxToken"));
		requestData.put("cmd", "prepareMarketplace");
		requestData.put("r1", request.getWood());
		requestData.put("r2", request.getClay());
		requestData.put("r3", request.getIron());
		requestData.put("r4", request.getCrop());
		requestData.put("dname", request.getDestinationVillage());
		requestData.put("id", data.get("id"));
		requestData.put("t", data.get("t"));
		requestData.put("X2", "1");
		requestData.put("ajaxToken", data.get("ajaxToken"));
		
		HttpRequest interTransferRequest = new HttpRequest();
		interTransferRequest.setCookies(request.getCookies());
		interTransferRequest.setStringData(bodyData.toString());
		interTransferRequest.setHttpMethod(HttpMethod.POST);
		interTransferRequest.setHost(request.getHost());
		interTransferRequest.setData(requestData);
		interTransferRequest.setPath("/ajax.php?cmd=prepareMarketplace");
		String response = httpService.ajax(interTransferRequest);
		if(Log.isDebugEnabled())
			Log.debug("interTransferResponse:::"+response);
		requestData.clear();
		requestData = ResourceUtil.parsePrepResourceTransferResponse(response);
		StringBuilder string = new StringBuilder();
		string.append("cmd=prepareMarketplace&")
		.append("t="+requestData.get("t")+"&")
		.append("id="+requestData.get("id")+"&")
		.append("a="+requestData.get("a")+"&")
		.append("sz="+requestData.get("sz")+"&")
		.append("kid="+requestData.get("kid")+"&")
		.append("c="+requestData.get("c")+"&")
		.append("x2=1"+"&")
		.append("r1="+request.getWood()+"&")
		.append("r2="+request.getClay()+"&")
		.append("r3="+request.getIron()+"&")
		.append("r4="+request.getCrop()+"&")
		.append("ajaxToken="+data.get("ajaxToken"));
		requestData.put("r1", request.getWood());
		requestData.put("r2", request.getClay());
		requestData.put("r3", request.getIron());
		requestData.put("r4", request.getCrop());
		requestData.put("dname", request.getDestinationVillage());
		requestData.put("ajaxToken", data.get("ajaxToken"));
		
		HttpRequest finalRequest = new HttpRequest();
		finalRequest.setCookies(request.getCookies());
		finalRequest.setStringData(string.toString());
		finalRequest.setData(requestData);
		finalRequest.setHttpMethod(HttpMethod.POST);
		finalRequest.setHost(request.getHost());
		finalRequest.setPath("/ajax.php?cmd=prepareMarketplace");
		String finalResponse = httpService.ajax(finalRequest);
		JSONObject jsonObj = new JSONObject(finalResponse);
		String notice = (String) ((JSONObject)((JSONObject)jsonObj.get("response")).get("data")).get("notice");
		if(notice.contains("Resources have been dispatched.")) {	
			return new Status("SUCCESS", 200);
		}
		return new Status((String) ((JSONObject)((JSONObject)jsonObj.get("response")).get("data")).get("errorMessage"), 500);
	}
	

}
