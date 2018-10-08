package com.lizikj.trace.trace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 日志追踪 自动配置的相关属性
 * @auth zone
 * @date 2017-10-14
 * 
 */
@ConfigurationProperties(prefix = "dubbo.trace")
@Configuration
public class TraceConfig {

    private boolean enabled=true;

    private int connectTimeout;

    private int readTimeout;

	private int threadPoolCount;// 线程数量

	private boolean threadPoolConfigurable = false;// 默认线程数量不可配置

    private int flushInterval=0;

    private boolean compressionEnabled=true;

    private String zipkinUrl;

    private int serverPort;

    private String applicationName;

    public boolean isThreadPoolConfigurable() {
		return threadPoolConfigurable;
	}

	public void setThreadPoolConfigurable(boolean threadPoolConfigurable) {
		this.threadPoolConfigurable = threadPoolConfigurable;
	}

	public int getServerPort(){
        return this.serverPort;
    }

    public String getApplicationName(){
        return this.applicationName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }

    public void setCompressionEnabled(boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

    public String getZipkinUrl() {
        return zipkinUrl;
    }

    public void setZipkinUrl(String zipkinUrl) {
        this.zipkinUrl = zipkinUrl;
    }

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public int getThreadPoolCount() {
		return threadPoolCount;
	}

	public void setThreadPoolCount(int threadPoolCount) {
		this.threadPoolCount = threadPoolCount;
	}
}
