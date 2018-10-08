package com.lizikj.trace.trace.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.lizikj.trace.annotation.EnableTraceAutoConfigurationProperties;
import com.lizikj.trace.context.TraceContext;
import com.lizikj.trace.trace.TraceAgent;

/**
 * 日志追踪自动配置开关
 * @auth zone
 * @date 2017-10-14
 */
@Configuration
@ConditionalOnBean(annotation = EnableTraceAutoConfigurationProperties.class)
@AutoConfigureAfter(SpringBootConfiguration.class)
@EnableConfigurationProperties(TraceConfig.class)
public class EnableTraceAutoConfiguration {

    @Autowired
    private TraceConfig traceConfig;

    @PostConstruct
    public void init() throws Exception {
        TraceContext.init(this.traceConfig);
        TraceAgent.init(this.traceConfig.getZipkinUrl());
    }
}
