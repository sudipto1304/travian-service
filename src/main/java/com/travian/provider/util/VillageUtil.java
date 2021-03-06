package com.travian.provider.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.travian.provider.request.TradeRouteRequest;
import com.travian.provider.response.Building;
import com.travian.provider.response.Fields;
import com.travian.provider.response.HttpResponse;
import com.travian.provider.response.IncomingAttack;
import com.travian.provider.response.Resource;
import com.travian.provider.response.Village;
import com.travian.provider.response.VillageTroop;

public class VillageUtil {

	private static final Logger Log = LoggerFactory.getLogger(VillageUtil.class);

	private static boolean isSameField = false;

	
	public static void parseDorf1(HttpResponse response, Village village) {
		parseResource(response, village);
		parseCommonAttributes(response, village);
		parseVillageTroops(response, village);
		parseIncomingAttack(response, village);
	}
	
	
	public static void parseDorf2(HttpResponse response, Village village) {
		parseBuildingResponse(response, village);
	}
	
	
	private static void parseResource(HttpResponse response, Village village) {
		Resource resource = new Resource();
		Document doc = Jsoup.parse(response.getBody());
		String resourceElementsStr = doc.select("map#rx").html();
		resourceElementsStr = resourceElementsStr.replaceAll("/&gt;", "</area>")
				.replaceAll("<span class=\" level\">", "").replaceAll(":", "\"").replaceAll("<br>", ">");
		Elements elm = Jsoup.parse(resourceElementsStr).select("area").parents();
		List<Node> nodes = elm.get(0).childNodes();
		List<Fields> fields = new ArrayList<Fields>();
		Fields field = null;
		for (Node node : nodes) {
			if (!(node instanceof TextNode)) {
				if (!isSameField) {
					field = new Fields();
					getFieldInfo(node, field);
					if (!field.isUpgradable()) {
						fields.add(field);
					}
				} else {
					fields.add(getFieldInfo(node, field));
				}
			}
		}
		resource.setFields(fields);
		resource.setWarehouseCapacity(Integer.valueOf(doc.select("span#stockBarWarehouse").text().replace(")", "")
				.replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setGranaryCapacity(Integer.valueOf(doc.select("span#stockBarGranary").text().replace(")", "")
				.replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setWood(Integer.valueOf(doc.select("span#l1").text().replace(")", "").replaceAll("\\u202C", "")
				.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setClay(Integer.valueOf(doc.select("span#l2").text().replace(")", "").replaceAll("\\u202C", "")
				.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setIron(Integer.valueOf(doc.select("span#l3").text().replace(")", "").replaceAll("\\u202C", "")
				.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setCrop(Integer.valueOf(doc.select("span#l4").text().replace(")", "").replaceAll("\\u202C", "")
				.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		resource.setCropUsage(Integer.valueOf(doc.select("span#stockBarFreeCrop").text().replace(")", "")
				.replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
		Elements tdElms = doc.select("table#production > tbody > tr > td.num");
		int index = 0;
		for (Element tdElm : tdElms) {
			if (index == 0)
				resource.setWoodProduction(
						Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "")
								.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			if (index == 1)
				resource.setClayProduction(
						Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "")
								.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			if (index == 2)
				resource.setIronProduction(
						Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "")
								.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			if (index == 3)
				resource.setCropProduction(
						Integer.valueOf(tdElm.text().trim().replace(")", "").replaceAll("\\u202C", "")
								.replaceAll("\\u202D", "").replaceAll("\\u2212", "-").replaceAll(",", "")));
			index++;
		}
		village.setOngoingConstruction(doc.select("div.buildDuration").size());

		village.setResource(resource);
	}

	private static Fields getFieldInfo(Node node, Fields field) {

		Element element = (Element) node;
		if (element.hasAttr("href") && !"Buildings".equals(element.attr("title"))) {
			field.setLink(element.attr("href"));
			field.setId(Integer.valueOf(field.getLink().replace("build.php?id=", "")));
			String title = element.attr("title");
			field.setType(title.substring(0, title.indexOf("Level")).trim());
			field.setLevel(Integer.valueOf(title.substring(title.indexOf("Level") + 5, title.indexOf("||")).trim()));
			if (title.contains("Completely upgraded")) {
				field.setUpgradable(false);
				field.setNextLevelWood(0);
				field.setNextLevelClay(0);
				field.setNextLevelIron(0);
				field.setNextLevelCrop(0);
			} else {
				field.setUpgradable(true);
				isSameField = true;
			}

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

	private static void parseCommonAttributes(HttpResponse response, Village village) {
		Document doc = Jsoup.parse(response.getBody());
		village.setVillageName(doc.select("div#villageNameField").text());
		village.setActive(true);

		village.setLoyalty(Integer.valueOf(doc.select("div.loyalty > span").text().replaceAll("%", "")
				.replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll(",", "")));
		Elements villageListElm = doc
				.select("div#sidebarBoxVillagelist > div.sidebarBoxInnerBox > div.content > ul > li");
		for (Element elm : villageListElm) {
			if (elm.select("a").hasClass("active")) {
				String y = elm.select("a > span.coordinates > span.coordinateY").text().replace(")", "")
						.replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-")
						.replaceAll(",", "");
				String x = elm.select("a > span.coordinates > span.coordinateX").text().replace("(", "")
						.replaceAll("\\u202C", "").replaceAll("\\u202D", "").replaceAll("\\u2212", "-")
						.replaceAll(",", "");
				village.setY(Integer.valueOf(y));
				village.setX(Integer.valueOf(x));
			}
		}

	}

	private static void parseBuildingResponse(HttpResponse response, final Village village) {
		String buildingElementStr = Jsoup.parse(response.getBody()).select("div#village_map").html();
		buildingElementStr = buildingElementStr.replaceAll("&gt;", "</div>").replaceAll("<span class=\" level\">", "")
				.replaceAll(":", "\"").replaceAll("<br>", ">");
		Elements buildingElm = Jsoup.parse(buildingElementStr).select("div.colorLayer");
		List<Building> buildings = new ArrayList<Building>();
		buildingElm.forEach(e -> {
			Building building = new Building();
			String levelStr = e.select("div.colorLayer").text();
			String titleStr = e.select("div.colorLayer").attr("title");
			if (StringUtils.isNoneEmpty(titleStr)) {
				String level = titleStr.substring(titleStr.indexOf("Level") + 6, titleStr.indexOf("||"));
				String[] resources = levelStr.split(" ");
				if (!titleStr.contains("Completely upgraded")) {
					if (levelStr.contains("Currently upgrading")) {
						building.setUpgrading(true);
						if (!levelStr.contains("Construction of maximum possible building")) {
							building.setNextLevelWood(Integer.valueOf(resources[resources.length - 5]));
							building.setNextLevelClay(Integer.valueOf(resources[resources.length - 4]));
							building.setNextLevelIron(Integer.valueOf(resources[resources.length - 3]));
							building.setNextLevelCrop(Integer.valueOf(resources[resources.length - 2]));
							building.setUpgradable(true);
						}
					} else {
						building.setNextLevelWood(Integer.valueOf(resources[resources.length - 5]));
						building.setNextLevelClay(Integer.valueOf(resources[resources.length - 4]));
						building.setNextLevelIron(Integer.valueOf(resources[resources.length - 3]));
						building.setNextLevelCrop(Integer.valueOf(resources[resources.length - 2]));
						building.setUpgradable(true);
					}
				}
				building.setBuildingName(titleStr.substring(0, titleStr.indexOf("Level")).trim());
				building.setBuildingLevel(Integer.valueOf(level));
				building.setLink(e.select("div.colorLayer").attr("onclick").replaceAll("window.location.href=", "")
						.replaceAll("\'", ""));
				String id = building.getLink().replace("build.php?id=", "");
				building.setId(Integer.valueOf(id));
				if ("Town Hall".equals(building.getBuildingName())) {
					village.setTownHallPresent(true);
					village.setThId(Integer.valueOf(building.getId()));
				}
				buildings.add(building);

			}
			if (Log.isDebugEnabled())
				Log.debug(titleStr + "::::" + levelStr);
		});
		village.setBuildings(buildings);
	}

	private static void parseVillageTroops(HttpResponse response, final Village village) {
		Document doc = Jsoup.parse(response.getBody());
		List<VillageTroop> villageTroops = new ArrayList<VillageTroop>();
		Elements troopCountElms = doc.select("table#troops > tbody > tr > td.num");
		Elements troopTypeElms = doc.select("table#troops > tbody > tr > td.un");
		for (int i = 0; i < troopCountElms.size(); i++) {
			VillageTroop troop = new VillageTroop();
			troop.setTroopType(troopTypeElms.get(i).text());
			troop.setTroopCount(Integer.valueOf(troopCountElms.get(i).text()));
			villageTroops.add(troop);
		}
		village.setVillageTroops(villageTroops);
	}

	private static void parseIncomingAttack(HttpResponse response, final Village village) {
		Document doc = Jsoup.parse(response.getBody());

		Elements incomingTroop = doc.select("span.a1");
		if (incomingTroop != null && incomingTroop.size() > 0) {
			String text = incomingTroop.get(0).text();
			Element parentTd = incomingTroop.get(0).parent().parent();
			String duration = parentTd.select("div.dur_r > span.timer").get(0).attr("value");
			Element parentTr = parentTd.parent();
			String link = parentTr.select("td.typ > a").attr("href");
			IncomingAttack attack = new IncomingAttack();
			attack.setAttackText(text);
			attack.setAttackCount(Integer.valueOf(text.replace("Attack", "").replace("Attacks", "").trim()));
			attack.setDuration(Long.valueOf(duration));
			attack.setLink(link);
			village.setIncomingAttack(attack);
		}

	}

	public static void parseMarketPlaceTradeRoutes(HttpResponse response, final TradeRouteRequest request) {
		Document doc = Jsoup.parse(response.getBody());
		Elements hiddenElm = doc.select("input[type=hidden]");
		hiddenElm.forEach(e -> {
			if ("gid".equals(e.attr("name"))) {
				request.setGid(e.attr("value"));
			}
			if ("a".equals(e.attr("name"))) {
				request.setA(e.attr("value"));
			}
			if ("t".equals(e.attr("name"))) {
				request.setT(e.attr("value"));
			}
			if ("trid".equals(e.attr("name"))) {
				request.setTrid(e.attr("value"));
			}
			if ("option".equals(e.attr("name"))) {
				request.setOption(e.attr("value"));
			}
		});
	}

	public static List<String> getTradeRoutes(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		Elements elms = doc.select("td.sel > a");
		List<String> routes = new ArrayList<String>();
		elms.forEach(e -> {
			routes.add(e.attr("href"));
		});
		return routes;
	}

	public static Map<String, Object> parseTHResponse(HttpResponse response) {
		Map<String, Object> thResponse = new HashMap<>();
		Document doc = Jsoup.parse(response.getBody());
		Elements holdButton = doc.select("button");
		thResponse.put("isCelebrationPossible", false);
		holdButton.forEach(e -> {
			if ("Hold".equals(e.attr("value"))) {
				thResponse.put("isCelebrationPossible", true);
				String link = e.attr("onclick");
				link = link.substring(link.indexOf("build"));
				link = link.substring(0, link.indexOf("'"));
				if (Log.isInfoEnabled())
					Log.info("TownHall Celebration Link::" + link);
				thResponse.put("link", link);
			}
		});
		return thResponse;
	}

	public static String getFinishTime(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		Elements elements = doc.select("td.dur > span.timer");
		if (elements.isEmpty()) {
			return "";
		} else {
			return elements.get(0).text();
		}
	}
	
	
	public static Map<String, String> parseSendTroopResponse(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		Map<String, String> parsedResponse = new HashMap<>();
		Elements hiddenElm = doc.select("input[type=hidden]");
		hiddenElm.forEach(e->{
			if("timestamp".equals(e.attr("name"))) {
				parsedResponse.put("timestamp", e.attr("value"));
			}
			if("timestamp_checksum".equals(e.attr("name"))) {
				parsedResponse.put("timestamp_checksum", e.attr("value"));
			}
			if("b".equals(e.attr("name"))) {
				parsedResponse.put("b", e.attr("value"));
			}
			if("currentDid".equals(e.attr("name"))) {
				parsedResponse.put("currentDid", e.attr("value"));
			}
			
		});
		parsedResponse.put("t1", doc.select("input[name=t1]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t2", doc.select("input[name=t2]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t3", doc.select("input[name=t3]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t4", doc.select("input[name=t4]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t5", doc.select("input[name=t5]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t6", doc.select("input[name=t6]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t7", doc.select("input[name=t7]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t8", doc.select("input[name=t8]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t9", doc.select("input[name=t9]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		parsedResponse.put("t10", doc.select("input[name=t10]").get(0).parent().select("a").text().replaceAll("\\u202C", "").replaceAll("\\u202D", ""));
		return parsedResponse;
		
	}
	
	public static Map<String, String> parseSendTroopConfirmResponse(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		Map<String, String> parsedResponse = new HashMap<>();
		Elements hiddenElm = doc.select("input[type=hidden]");
		hiddenElm.forEach(e->{
			parsedResponse.put(e.attr("name"), e.attr("value"));
		});
		parsedResponse.put("s1", "ok");
		return parsedResponse;
	}
	
	public static String getCrossLink(HttpResponse response) {
		Document doc = Jsoup.parse(response.getBody());
		String link = doc.select("button[title=cancel]").get(0).attr("onclick");
		return link.substring(link.indexOf("\'")+1, link.indexOf(";")-1);
	}
}
