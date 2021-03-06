package com.travian.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("internet.proxy")
@Data
public class ProxyProperties {
	
	private boolean enable;
	private String host;
	private String port;

}
