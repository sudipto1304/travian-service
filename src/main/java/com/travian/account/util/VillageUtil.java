package com.travian.account.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.travian.account.response.Fields;
import com.travian.account.response.HttpResponse;
import com.travian.account.response.Resource;
import com.travian.account.response.Village;

public class VillageUtil {
	
	private static boolean isSameField = false;

	public static Resource parseResource(HttpResponse response) {
		Resource resource = new Resource();
		Document doc = Jsoup.parse(response.getBody());
		String resourceElementsStr = doc.select("map#rx").html();
		resourceElementsStr = resourceElementsStr.replaceAll("/&gt;", "</area>").replaceAll("<span class=\" level\">", "").replaceAll(":", "\"").replaceAll("<br>", ">");
		Elements elm = Jsoup.parse(resourceElementsStr).select("area").parents();
		List<Node> nodes = elm.get(0).childNodes();
		List<Fields> fields = new ArrayList<Fields>();
		Fields field = null;
		for(Node node : nodes) {
			if (!(node instanceof TextNode)) {
				if(!isSameField) {
					field = new Fields();
					getFieldInfo(node, field);
				}else {
					fields.add(getFieldInfo(node, field));
				}
			}
		}
		resource.setFields(fields);
		resource.setWarehouseCapacity(Integer.valueOf(doc.select("span#stockBarWarehouse").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setGranaryCapacity(Integer.valueOf(doc.select("span#stockBarGranary").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setWood(Integer.valueOf(doc.select("span#l1").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setClay(Integer.valueOf(doc.select("span#l2").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setIron(Integer.valueOf(doc.select("span#l3").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setCrop(Integer.valueOf(doc.select("span#l4").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setCropUsage(Integer.valueOf(doc.select("span#stockBarFreeCrop").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		Elements tdElms = doc.select("td.num");
		int index=0;
		for(Element tdElm : tdElms) {
			if(index==0)
				resource.setWoodProduction(Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			if(index==1)
				resource.setClayProduction(Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			if(index==2)
				resource.setIronProduction(Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			if(index==3)
				resource.setCropProduction(Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			index++;
		}
		
		

		
		return resource;
	}

	private static Fields getFieldInfo(Node node, Fields field) {
		
			Element element = (Element) node;
			if (element.hasAttr("href") && !"Buildings".equals(element.attr("title"))) {
				field.setLink(element.attr("href"));
				field.setId(Integer.valueOf(field.getLink().replace("build.php?id=", "")));
				String title = element.attr("title");
				field.setType(title.substring(0, title.indexOf("Level")).trim());
				field.setLevel(
						Integer.valueOf(title.substring(title.indexOf("Level") + 5, title.indexOf("||")).trim()));
				isSameField = true;
			} else {
				Elements resourceElements = element.select("div.resourceWrapper > div.resources");
				for (Element resourceElement : resourceElements) {
					String resourceId = resourceElement.select("i").attr("class");
					String resourceAmount = resourceElement.select("span").text();
					if ("r1".equals(resourceId))
						field.setNextLevelWood(Integer.valueOf(resourceAmount));
					if ("r2".equals(resourceId))
						field.setNextLevelClay(Integer.valueOf(resourceAmount));
					if ("r3".equals(resourceId))
						field.setNextLevelIron(Integer.valueOf(resourceAmount));
					if ("r4".equals(resourceId))
						field.setNextLevelCrop(Integer.valueOf(resourceAmount));
				}
				isSameField = false;
			}
	
		return field;
	}
	
	public static void parseCommonAttributes(HttpResponse response, Village village) {
		Document doc = Jsoup.parse(response.getBody());
		village.setVillageName(doc.select("div#villageNameField").text());
		village.setActive(true);
		village.setLoyalty(Integer.valueOf(doc.select("div.loyalty > span").text().replaceAll("%", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll(",", "")));
		Elements villageListElm = doc.select("div#sidebarBoxVillagelist > div.sidebarBoxInnerBox > div.content > ul > li");
		for(Element elm : villageListElm) {
			if(elm.select("a").hasClass("active")) {
				String y = elm.select("a > span.coordinates > span.coordinateY").text().replace(")", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "");
				String x = elm.select("a > span.coordinates > span.coordinateX").text().replace("(", "").replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "");
				village.setY(Integer.valueOf(y));
				village.setX(Integer.valueOf(x));
			}
		}
		
	}

}
