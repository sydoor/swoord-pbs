package com.lizikj.mq.aliware.config;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQClientException;
import com.lizikj.mq.aliware.core.AliwareDispatchMessageListener;
import com.lizikj.mq.common.AliwaremqCondition;
import com.lizikj.mq.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Configuration
@Conditional(AliwaremqCondition.class)
public class AliwareConsumerConfig {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private AliwareProperties aliwareConfig;

    @Value("${consumer.topics:NO_CONFIG}")
    private String topics;

    @Value("${mq.env:PROD}")
    private String mqEnvironment;

    @Autowired
    private AliwareDispatchMessageListener listener;

    @Bean(destroyMethod = "shutdown")
    public Consumer defaultAliwareMQConsumer() throws MQClientException {
        if (topics.equals("NO_CONFIG")) {
            log.warn("aliware mq consumer.topics没有配置. aliwareMQConsumer没有启动。");
            return null;
        }

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ConsumerId, aliwareConfig.getConsumerId());
        properties.put(PropertyKeyConst.AccessKey, aliwareConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, aliwareConfig.getSecretKey());
        Consumer consumer = ONSFactory.createConsumer(properties);
        String suffix = "_" + mqEnvironment.toUpperCase();
        String[] topic = topics.split(",");
        for (String atopic : topic) {
            log.info("aliware mq adding topics. topic={}", atopic);

            String topicName = atopic + suffix;
            consumer.subscribe(topicName, "*", listener);
        }
        return consumer;
    }

}
