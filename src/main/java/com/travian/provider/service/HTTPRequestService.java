package com.travian.provider.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travian.provider.config.ProxyProperties;
import com.travian.provider.request.HttpRequest;
import com.travian.provider.response.HttpResponse;

@Service
public class HTTPRequestService {

	private static final Logger Log = LoggerFactory.getLogger(HTTPRequestService.class);

	@Autowired
	private ProxyProperties proxyProp;

	public HttpResponse get(HttpRequest request) throws IOException {
		String url = "https://" + request.getHost();
		if(request.getPath()!=null) {
			url=url+request.getPath();
		}
		HttpResponse response = new HttpResponse();
		Connection.Response res = null;
		if (proxyProp.isEnable()) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					InetSocketAddress.createUnresolved(proxyProp.getHost(), Integer.valueOf(proxyProp.getPort()))

			);
			if (request.getCookies() != null && !request.getCookies().isEmpty()) {
				res = Jsoup.connect(url).proxy(proxy).cookies(request.getCookies()).method(Connection.Method.GET)
						.execute();
			} else {
				res = Jsoup.connect(url).proxy(proxy).method(Connection.Method.GET).execute();
			}
		} else {
			if (request.getCookies() != null && !request.getCookies().isEmpty()) {
				res = Jsoup.connect(url).cookies(request.getCookies()).method(Connection.Method.GET).execute();
			} else {
				res = Jsoup.connect(url).method(Connection.Method.GET).execute();
			}
		}

		response.setBody(StringEscapeUtils.unescapeHtml4(res.body()));
		response.setCookies(res.cookies());
		response.setHttpStatus(res.statusMessage());
		response.setHttpStatusCode(res.statusCode());
		if (Log.isDebugEnabled()) {
			Log.debug("Cookies:::" + res.cookies());
			Log.debug("BodyResponse:::" + response.getBody());
		}
		return response;
	}

	public HttpResponse post(HttpRequest request) throws IOException {
		String url = "https://" + request.getHost() + request.getPath();
		HttpResponse response = new HttpResponse();
		Connection.Response res = null;
		if (proxyProp.isEnable()) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,
					InetSocketAddress.createUnresolved(proxyProp.getHost(), Integer.valueOf(proxyProp.getPort()))

			);
			if (request.getCookies() != null && !request.getCookies().isEmpty()) {
				res = Jsoup.connect(url).proxy(proxy).cookies(request.getCookies()).data(request.getData())
						.method(Connection.Method.POST).execute();
			} else {
				res = Jsoup.connect(url).proxy(proxy).data(request.getData()).method(Connection.Method.POST).execute();
			}
		} else {
			if (request.getCookies() != null && !request.getCookies().isEmpty()) {
				res = Jsoup.connect(url).cookies(request.getCookies()).data(request.getData())
						.method(Connection.Method.POST).execute();
			} else {
				res = Jsoup.connect(url).data(request.getData()).method(Connection.Method.POST).execute();
			}
		}

		response.setBody(StringEscapeUtils.unescapeHtml4(res.body()));
		response.setCookies(res.cookies());
		response.setHttpStatus(res.statusMessage());
		response.setHttpStatusCode(res.statusCode());
		if (Log.isDebugEnabled()) {
			Log.debug("Cookies:::" + res.cookies());
			Log.debug("BodyResponse:::" + response.getBody());
		}
		return response;
	}

}
