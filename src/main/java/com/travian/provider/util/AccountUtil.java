package com.travian.provider.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.travian.provider.response.AccountInfoResponse;
import com.travian.provider.response.Adventure;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.UserInfo;
import com.travian.provider.response.Village;

public class AccountUtil {
	
	public static AccountInfoResponse parseAccountResponse(HttpResponse loginResponse) {
		AccountInfoResponse response = new AccountInfoResponse();
		UserInfo user = new UserInfo();
		List<Village> villages = new ArrayList<>();
		Document doc = Jsoup.parse(loginResponse.getBody());
		Elements userInfoElement = doc.select("div.playerName > a");
		user.setTribe(userInfoElement.get(0).getElementsByClass("nation").attr("title"));
		user.setLink("/" + userInfoElement.get(1).attr("href"));
		user.setUserName(userInfoElement.get(1).text());
		response.setUser(user);
		response.setCookies(loginResponse.getCookies());
		Elements villageListElm = doc.select("div#sidebarBoxVillagelist > div.sidebarBoxInnerBox > div.content > ul > li");
		for(Element elm : villageListElm) {
			Village village = new Village();
			if(elm.select("a").hasClass("active")) {
				village.setActive(true);
				village.setLoyalty(Integer.valueOf(doc.select("div.loyalty > span").text().replaceAll("%", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll(",", "")));
			}
			village.setLink(elm.select("a").attr("href"));
			village.setVillageName(elm.select("a > div.name").text());
			String y = elm.select("a > span.coordinates > span.coordinateY").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "");
			String x = elm.select("a > span.coordinates > span.coordinateX").text().replace("(", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "");
			village.setY(Integer.valueOf(y));
			village.setX(Integer.valueOf(x));
			
			villages.add(village);
		}
		response.setVillages(villages);
		if(doc.select("button.adventureWhite > div.speechBubbleContainer > div.speechBubbleContent")!=null && StringUtils.isNotEmpty(doc.select("button.adventureWhite > div.speechBubbleContainer > div.speechBubbleContent").text()))
			response.setPendingAdventure(Integer.valueOf(doc.select("button.adventureWhite > div.speechBubbleContainer > div.speechBubbleContent").text()));
		
		String temp = doc.select("div.heroHealthBarBox > div.bar").attr("style");
		temp = temp.substring(temp.indexOf(":")+1, temp.indexOf("%"));
		response.setHeroHealth(Integer.valueOf(temp));
		response.setSilver(Integer.valueOf(doc.select("span.ajaxReplaceableSilverAmount").text().replaceAll(",", "")));
		response.setGold(Integer.valueOf(doc.select("span.ajaxReplaceableGoldAmount").text().replaceAll(",", "")));
		response.setAlliance(doc.select("div#sidebarBoxAllianceNoNews > div.sidebarBoxInnerBox > div.innerBox > div.boxTitle").text());
		response.setHeroStatus(doc.select("div.heroStatusMessage").text());
		return response;
	}
	
	
	public static List<Adventure> parseAdventure(HttpResponse adventureResponse){
		
	}

}
