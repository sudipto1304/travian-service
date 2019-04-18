package com.travian.provider.util;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.travian.provider.response.HttpResponse;

public class TrainingUtil {
	
	public static Map<String, String> getTroopCount(HttpResponse response, String troopType) {
		Document doc = Jsoup.parse(response.getBody());
		Map<String, String> data = new HashMap<String, String>();
		Elements elements = doc.select("div.cta");
		for(Element elm : elements) {
			if(troopType.equals(elm.select("input.text").attr("name"))) {
				Elements links = elm.select("a");
				data.put(troopType, links.get(0).text());
			}
		}
		data.put("id", doc.select("input[name=id]").attr("value"));
		data.put("z", doc.select("input[name=z]").attr("value"));
		data.put("a", doc.select("input[name=a]").attr("value"));
		data.put("s", doc.select("input[name=s]").attr("value"));
		data.put("did", doc.select("input[name=did]").attr("value"));
		data.put("s1", "ok");
		return data;
	}
	
	public static String getFinishTime(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		Elements elements = doc.select("td.dur > span.timer");
		return elements.get(elements.size()-1).text();
	}

}
