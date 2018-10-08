package com.lizikj.mq.rocket.config;

import com.lizikj.mq.common.RocketmqCondition;
import com.lizikj.mq.rocket.core.RocketDispatchMessageListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Configuration
@Conditional(RocketmqCondition.class)
public class RocketConsumerConfig {

    protected Logger log = LoggerFactory.getLogger(getClass());
    @Value("${consumer.topics:NO_CONFIG}")
    private String topics;
    @Autowired
    private RocketDispatchMessageListener listener;


    @Value("${mq.env:PROD}")
    private String mqEnvironment;


    @Bean(destroyMethod = "shutdown")
    @ConfigurationProperties(prefix = "rocket")
    public DefaultMQPushConsumer defaultMQPushConsumer() throws MQClientException {
        if (topics.equals("NO_CONFIG")) {
            log.warn("rocket mq consumer.topics没有配置. MQPushConsumer没有启动。");
            return null;
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        String[] topic = topics.split(",");
        String suffix = "_" + mqEnvironment.toUpperCase();
        for (String atopic : topic) {
            log.info("rocket mq adding topics. topic={}", atopic);
            String topicName = atopic + suffix;
            consumer.subscribe(topicName, "*");
        }
        consumer.setMessageListener(listener);
        return consumer;
    }
}
