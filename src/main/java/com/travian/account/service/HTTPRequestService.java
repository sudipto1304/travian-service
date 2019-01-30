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
		URL url = new URL("https://"+request.getHost());
		HttpResponse response = new HttpResponse();
		HttpURLConnection uc=null;
		if(proxyProp.isEnable()) {
			Proxy proxy = new Proxy(                                      
			        Proxy.Type.HTTP,                                     
			        InetSocketAddress.createUnresolved(proxyProp.getHost(), proxyProp.getPort())
			);
			uc= (HttpURLConnection)url.openConnection(proxy);
		}else {
			uc=(HttpURLConnection) url.openConnection();
		}
		uc.setRequestProperty("User-Agent", Constants.USER_AGENT);
		uc.setRequestProperty("Content-Language", "en-US");
		uc.setRequestMethod(request.getHttpMethod().name());
		if(request.getCookie()!=null && !request.getCookie().isEmpty()) {
			uc.setRequestProperty("Cookie", request.getCookie().stream().collect(Collectors.joining(",")));
		}
		uc.connect();
		InputStream is = null;
		
		is = new BufferedInputStream(uc.getInputStream());
		BufferedReader responseBr = new BufferedReader(new InputStreamReader(is));
		String inputLine = "";
		StringBuilder sb = new StringBuilder();
		while ((inputLine = responseBr.readLine()) != null) {
            sb.append(inputLine);
        }
		responseBr.close();
		response.setHttpStatus(uc.getResponseMessage());
		response.setHttpStatusCode(uc.getResponseCode());
		Map<String, List<String>> map = uc.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet())
		{
		    if ("Set-Cookie".equals(entry.getKey())) {
		    	List<String> headerValues = entry.getValue();
		    	response.setCookies(headerValues);
		    }
		}
		response.setBody(sb.toString());
		if(Log.isDebugEnabled()) {
			Log.debug(sb.toString());
		}
		return response;
	}
	
	public HttpResponse post(HttpRequest request) throws IOException {
		URL url = new URL("https://"+request.getHost()+"/"+request.getPath());
		HttpResponse response = new HttpResponse();
		HttpURLConnection uc=null;
		String param=null;
		if(proxyProp.isEnable()) {
			Proxy proxy = new Proxy(                                      
			        Proxy.Type.HTTP,                                     
			        InetSocketAddress.createUnresolved(proxyProp.getHost(), proxyProp.getPort())
			);
			uc= (HttpURLConnection)url.openConnection(proxy);
		}else {
			uc=(HttpURLConnection) url.openConnection();
		}
		uc.setRequestProperty("User-Agent", Constants.USER_AGENT);
		uc.setRequestProperty("Content-Language", "en-US,en;q=0.9");
		uc.setRequestProperty("content-type", "application/x-www-form-urlencoded");
		uc.setRequestMethod(request.getHttpMethod().name());
		String cookies = null;
		if(request.getCookie()!=null && !request.getCookie().isEmpty()) {
			cookies=request.getCookie().stream().findFirst().get();
			uc.setRequestProperty("cookie", cookies);
		}
		if(request.getData()!=null && !request.getData().isEmpty()) {
			uc.setDoOutput(true);
			uc.setDoInput(true);
			param = URLEncoder.encode(request.getData().entrySet().stream().map(e->e.getKey()+"="+e.getValue()).collect(Collectors.joining("&")), "UTF-8");
			uc.setRequestProperty("content-length", String.valueOf(param.length()));
		}
		
		uc.setRequestProperty("cache-control", "max-age=0");
		uc.setRequestProperty("upgrade-insecure-requests", "1");
		uc.setRequestProperty("referer", "https://"+request.getHost()+"/");
		uc.setRequestProperty("origin", "https://"+request.getHost()+"/");
		
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(uc.getOutputStream());
        outputStreamWriter.write(param);
        outputStreamWriter.flush();
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String inputLine;
		StringBuilder sb = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
		in.close();
		response.setHttpStatus(uc.getResponseMessage());
		response.setHttpStatusCode(uc.getResponseCode());
		Map<String, List<String>> map = uc.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet())
		{
		    if ("Set-Cookie".equals(entry.getKey())) {
		    	List<String> headerValues = entry.getValue();
		    	response.setCookies(headerValues);
		    }
		}
		response.setBody(sb.toString());
		if(Log.isDebugEnabled()) {
			Log.debug(sb.toString());
		}
		return response;
	}

}
