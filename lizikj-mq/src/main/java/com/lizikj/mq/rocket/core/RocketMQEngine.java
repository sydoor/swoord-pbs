package com.lizikj.mq.rocket.core;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import com.lizikj.mq.common.RocketmqCondition;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Component
@Conditional(RocketmqCondition.class)
public class RocketMQEngine {

	@Value("${mq.env:PROD}")
	private String mqEnvironment;
	  
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired(required = false)
    private MQProducer mqProducer;

    @Autowired(required = false)
    private MQPushConsumer consumer;
    
    @PostConstruct
    public void start() throws MQClientException {
		if (consumer != null) {
			consumer.start();
			log.info("consumer start successfully!");
		}
		if (mqProducer != null) {
			mqProducer.start();
			log.info("producer start successfully!");
		}
    }
}
