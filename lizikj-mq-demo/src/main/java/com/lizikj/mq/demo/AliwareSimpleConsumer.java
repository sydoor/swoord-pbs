package com.lizikj.mq.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lizikj.mq.constants.Tags;
import com.lizikj.mq.constants.Topic;
import com.lizikj.mq.consumer.MessageSubscriber;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Component
public class AliwareSimpleConsumer extends MessageSubscriber {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
    @Override
    public void consume(String message) {
    	logger.info("aliware SimpleConsumer:" + message);
    }

    @Override
    public String followTags() {

        return Tags.HELLO.toString();
    }

    @Override
    public String followTopic() {

        //return Topic.DEMO.toString();
    	return Topic.GZLZTEST_0524.toString();
    }

}
