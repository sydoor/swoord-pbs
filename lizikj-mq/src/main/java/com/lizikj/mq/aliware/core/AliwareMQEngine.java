package com.lizikj.mq.aliware.core;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.lizikj.mq.common.AliwaremqCondition;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Component
@Conditional(AliwaremqCondition.class)
public class AliwareMQEngine {

	@Value("${mq.env:PROD}")
	private String mqEnvironment;
	  
    protected Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired(required = false)    
    private ProducerBean aliwareProducer;
    
    @Autowired(required = false)    
    private Consumer aliwareConsumer;
    
    @PostConstruct
    public void start() throws MQClientException {
    	
    	if(aliwareConsumer != null){
    		aliwareConsumer.start();
    		log.info("aliware mq consumer start successfully!");
    	}
    	
		if (aliwareProducer != null) {
			aliwareProducer.start();
			log.info("aliware mq producer start successfully!");
		}
		return;
    }
}
