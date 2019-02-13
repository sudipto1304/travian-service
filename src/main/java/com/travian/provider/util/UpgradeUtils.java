package com.travian.provider.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.travian.provider.response.HttpResponse;

public class UpgradeUtils {
	
	
	public static String getUpgradePath(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		Elements buttonElement = doc.select("div.section1 > button.green");
		String link=null;
		if(buttonElement!=null) {
			link = buttonElement.attr("onclick");
			link = link.substring(link.indexOf("\'")+1, link.length());
			link = link.substring(0, link.indexOf("\'"));
		}
		return link;
	}
	
	public static int getOnGoingUpgradeCount(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		return doc.select("div.buildDuration").size();
	}

}
