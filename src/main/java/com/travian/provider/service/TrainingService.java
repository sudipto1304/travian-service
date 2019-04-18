package com.travian.provider.service;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.travian.provider.request.HttpRequest;
import com.travian.provider.request.TroopTrainRequest;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.TroopTrainResponse;
import com.travian.provider.util.TrainingUtil;

@Service
public class TrainingService {

	private static final Logger Log = LoggerFactory.getLogger(TrainingService.class);

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

}
