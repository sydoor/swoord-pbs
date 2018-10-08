package com.lizikj.mq.aliware.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="aliware")
public class AliwareProperties {
	private String consumerId;
	private String producerId;
	private String accessKey;
	private String secretKey;
	private String onsAddr;
	
	public String getProducerId() {
		return producerId;
	}
	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getOnsAddr() {
		return onsAddr;
	}
	public void setOnsAddr(String onsAddr) {
		this.onsAddr = onsAddr;
	}
	public String getConsumerId() {
		return consumerId;
	}
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	
}
