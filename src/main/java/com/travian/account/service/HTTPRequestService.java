package com.travian.account.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travian.account.config.ProxyProperties;
import com.travian.account.request.HttpRequest;
import com.travian.account.response.HttpResponse;
import com.travian.account.util.Constants;

@Service
public class HTTPRequestService {
	
	private static final Logger Log = LoggerFactory.getLogger(HTTPRequestService.class);

	
	@Autowired
	private ProxyProperties proxyProp;
	
	
	
	public HttpResponse get(HttpRequest request) throws IOException {
		String url = "https://"+request.getHost();
		HttpResponse response = new HttpResponse();
		Connection.Response res=null;
		if(proxyProp.isEnable()) {
			System.setProperty("http.proxyHost", proxyProp.getHost());
			System.setProperty("http.proxyPort", proxyProp.getPort());
		}
		if(request.getCookies()!=null && !request.getCookies().isEmpty()) {
			res = Jsoup.connect(url).cookies(request.getCookies()).method(Connection.Method.GET).execute();
		}else {
			res = Jsoup.connect(url).method(Connection.Method.GET).execute();
		}
		
		response.setBody(res.body());
		response.setCookies(res.cookies());
		response.setHttpStatus(res.statusMessage());
		response.setHttpStatusCode(res.statusCode());
		if(Log.isDebugEnabled()) {
			Log.debug("Cookies:::"+res.cookies());
			Log.debug("BodyResponse:::"+res.body());
		}
		return response;
	}
	
	public HttpResponse post(HttpRequest request) throws IOException {
		String url = "https://"+request.getHost()+request.getPath();
		HttpResponse response = new HttpResponse();
		Connection.Response res=null;
		if(proxyProp.isEnable()) {
			System.setProperty("http.proxyHost", proxyProp.getHost());
			System.setProperty("http.proxyPort", proxyProp.getPort());
		}
		if(request.getCookies()!=null && !request.getCookies().isEmpty()) {
			res = Jsoup.connect(url).cookies(request.getCookies()).data(request.getData()).method(Connection.Method.POST).execute();
		}else {
			res = Jsoup.connect(url).data(request.getData()).method(Connection.Method.POST).execute();
		}
		
		response.setBody(res.body());
		response.setCookies(res.cookies());
		response.setHttpStatus(res.statusMessage());
		response.setHttpStatusCode(res.statusCode());
		if(Log.isDebugEnabled()) {
			Log.debug("Cookies:::"+res.cookies());
			Log.debug("BodyResponse:::"+res.body());
		}
		return response;
	}

}
