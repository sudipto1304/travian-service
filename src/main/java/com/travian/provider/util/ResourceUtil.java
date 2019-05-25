package com.travian.provider.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.travian.provider.response.HttpResponse;

public class ResourceUtil {
	
	private static final Logger Log = LoggerFactory.getLogger(ResourceUtil.class);
	
	public static Map<String, String> parseSendResource(HttpResponse response) {
		Map<String, String> data = new HashMap<>();
		int index = response.getBody().indexOf("flummoxingGerminatesSaguaroSeventeens");
		String tempString = response.getBody().substring(index, response.getBody().length());
		if(Log.isDebugEnabled())
			Log.debug("TempString::"+tempString);
		
		tempString = tempString.replace("flummoxingGerminatesSaguaroSeventeens = function() {", "").replace("return '", "");
		tempString=tempString.substring(0, tempString.indexOf("'"));
		String ajaxId = tempString.trim();
		Document doc = Jsoup.parse(response.getBody());
		String capacity = doc.select("span#merchantCapacityValue").text();
		data.put("ajaxToken", ajaxId);
		data.put("capacity", capacity);
		data.put("cmd", "prepareMarketplace");
		data.put("id", doc.select("input#id").attr("value"));
		data.put("t", doc.select("input#t").attr("value"));
		return data;
		
	}
	
	public static Map<String, String> parsePrepResourceTransferResponse( String response) throws JSONException {
		JSONObject jsonObj = new JSONObject(response);
		Map<String, String> parsedMap = new HashMap<String, String>();
		String form = (String) ((JSONObject)((JSONObject)jsonObj.get("response")).get("data")).get("formular");
		Document doc = Jsoup.parse(form);
		parsedMap.put("cmd", "prepareMarketplace");
		parsedMap.put("t", doc.select("input#t").attr("value"));
		parsedMap.put("id", doc.select("input#id").attr("value"));
		parsedMap.put("a", doc.select("input#a").attr("value"));
		parsedMap.put("sz", doc.select("input#sz").attr("value"));
		parsedMap.put("kid", doc.select("input#kid").attr("value"));
		parsedMap.put("c", doc.select("input#c").attr("value"));
		parsedMap.put("x2", "1");
		return parsedMap;
	}

}
