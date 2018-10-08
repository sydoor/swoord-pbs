package com.lizikj.mq.demo;

import java.nio.charset.Charset;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lizikj.mq.constants.Tags;
import com.lizikj.mq.constants.Topic;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class MQTest extends BaseTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    MQProducer producer;


    @Test
    public void simple() throws Exception {
        String msg = "hello rotket mq!";
        Message message = new Message("TAS", Tags.HELLO.toString(), msg.getBytes(Charset.defaultCharset()));
        SendResult result = producer.send(message);
        logger.info(message + ":" +result.getSendStatus());
//		waitConsume(20);
    }

    @Test
    public void ext() throws Exception {
        String msg = "rich message";
        Message message = new Message(Topic.DEMO.toString(), Tags.EXT.toString(), msg.getBytes(Charset.defaultCharset()));
        SendResult result = producer.send(message);
        logger.info(message + " " +result.getSendStatus());
        waitConsume(20);
    }

    //不再支持
    @Test
    public void notRecallable() throws Exception {
//		String msg = "100";		
        for (int i = 1; i <= 10; i++) {
            String keys = "simplekeyabc" + i;
//			for (int j = 0; j < 3; j++) {
            Message message = new Message(Topic.DEMO.toString(), "TMD", ("msg" + i).getBytes(Charset.defaultCharset()));
            SendResult result = producer.send(message);
            logger.info(message+ " " +result.getSendStatus());
//			}	
        }
//		waitConsume(30);
    }

    


}
