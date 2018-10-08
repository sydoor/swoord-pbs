package com.lizikj.mq.rocket.config;

import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.lizikj.mq.common.RocketmqCondition;
import com.lizikj.mq.rocket.core.RocketForceBindKeysProducer;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Configuration
@Conditional(RocketmqCondition.class)
public class RocketProducerConfig {
	
    @Bean(destroyMethod = "shutdown")
    @ConfigurationProperties(prefix = "rocket")
    public TransactionMQProducer defaultMQProducer() {
        TransactionMQProducer mqProducer = new RocketForceBindKeysProducer();
        return mqProducer;
    }
}
