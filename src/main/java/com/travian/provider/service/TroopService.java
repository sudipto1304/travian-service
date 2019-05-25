package com.travian.provider.service;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.travian.provider.request.HttpRequest;
import com.travian.provider.request.TroopEvasionRequest;
import com.travian.provider.request.TroopTrainRequest;
import com.travian.provider.response.EvasionResponse;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.Status;
import com.travian.provider.response.TroopTrainResponse;
import com.travian.provider.util.TrainingUtil;
import com.travian.provider.util.VillageUtil;

@Service
public class TroopService {

	private static final Logger Log = LoggerFactory.getLogger(TroopService.class);

	@Autowired
	private HTTPRequestService httpService;

	public TroopTrainResponse trainTroop(TroopTrainRequest request) throws IOException {
		TroopTrainResponse response = new TroopTrainResponse();
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setCookies(request.getCookies());
		httpRequest.setHost(request.getHost());
		httpRequest.setHttpMethod(HttpMethod.GET);
		if(request.getPath()==null) {
			request.setPath("/build.php?newdid="+request.getVillageId()+"&id="+request.getBuildingId());
		}
		httpRequest.setPath(request.getPath());
		HttpResponse httpResponse = httpService.get(httpRequest);
		Map<String, String> data = TrainingUtil.getTroopCount(httpResponse, request.getTroopType());
		if (Integer.valueOf(data.get(request.getTroopType())) != 0) {
			response.setCount(Integer.valueOf(data.get(request.getTroopType())));
			HttpRequest trainingRequest = new HttpRequest();
			trainingRequest.setCookies(request.getCookies());
			trainingRequest.setData(data);
			trainingRequest.setHost(request.getHost());
			trainingRequest.setHttpMethod(HttpMethod.POST);
			trainingRequest.setPath("/build.php?id=" + request.getBuildingId());
			HttpResponse trainingResponse = httpService.post(trainingRequest);
			response.setVillageId(request.getVillageId());
			response.setTimeRequired(TrainingUtil.getFinishTime(trainingResponse));
			return response;
		}else {
			response.setVillageId(request.getVillageId());
			response.setCount(0);
			return response;
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
		if(Integer.valueOf(finalData.get("t1"))==0 && Integer.valueOf(finalData.get("t2"))==0 && Integer.valueOf(finalData.get("t3"))==0 && Integer.valueOf(finalData.get("t4"))==0 && Integer.valueOf(finalData.get("t5"))==0 && Integer.valueOf(finalData.get("t6"))==0 && Integer.valueOf(finalData.get("t7"))==0 && Integer.valueOf(finalData.get("t8"))==0 && Integer.valueOf(finalData.get("t9"))==0 && Integer.valueOf(finalData.get("t10"))==0 ) {
			if(Log.isInfoEnabled())
				Log.info("Fianl troop details data::"+finalData+" as there is no troop, no need to resolve attack");
			
			EvasionResponse response = new EvasionResponse();
			response.setStatus("NO.TROOP.PRESENT");
			response.setStatusCode(412);
			return response;
		}
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
