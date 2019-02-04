package com.travian.account.util;


import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.travian.account.response.HttpResponse;
import com.travian.account.response.Resource;

public class VillageUtil {
	
	public static Resource parseResource(HttpResponse response) {
		Resource resource = new Resource();
		Document doc = Jsoup.parse(response.getBody());
		String resourceElementsStr = doc.select("map#rx").html();
		resourceElementsStr = resourceElementsStr.replaceAll("/&gt;", "</area>").replaceAll("<span class=\" level\">", "").replaceAll(":", "\"").replaceAll("<br>", ">");
		Elements elm = Jsoup.parse(resourceElementsStr).select("area").parents();
		List<Node> nodes = elm.get(0).childNodes();
		nodes.forEach(e->{
			if(!(e instanceof TextNode)) {
				Element element = (Element) e;
				System.out.println(element);
			}
		});
		return resource;
	}

}
