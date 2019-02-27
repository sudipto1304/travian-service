package com.travian.provider.util;

import java.util.ArrayList;
import java.util.List;

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
import com.travian.provider.response.Resource;
import com.travian.provider.response.Village;
import com.travian.provider.response.VillageTroop;

public class VillageUtil {

	private static final Logger Log = LoggerFactory.getLogger(VillageUtil.class);

	private static boolean isSameField = false;

	public static void parseResource(HttpResponse response, Village village) {
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
					if(!field.isUpgradable()) {
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

	public static void parseCommonAttributes(HttpResponse response, Village village) {
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

	public static void parseBuildingResponse(HttpResponse response, final Village village) {
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
				buildings.add(building);
			}
			if (Log.isDebugEnabled())
				Log.debug(titleStr + "::::" + levelStr);
		});
		village.setBuildings(buildings);
	}

	public static void parseVillageTroops(HttpResponse response, final Village village) {
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
	
	public static List<String> getTradeRoutes(HttpResponse response){
		Document doc = Jsoup.parse(response.getBody());
		Elements elms = doc.select("td.sel > a");
		List<String> routes = new ArrayList<String>();
		elms.forEach(e->{
			routes.add(e.attr("href"));
		});
		return routes;
	}
}
