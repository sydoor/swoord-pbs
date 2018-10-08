package com.lizikj.mq.demo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lizikj.mq.constants.Tags;
import com.lizikj.mq.constants.Topic;
import com.lizikj.mq.producer.MessagePublisher;
/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class PublisherTest extends BaseTest {
    @Autowired
    MessagePublisher publisher;
    
    @Test
    public void simple() throws Exception {
        String msg = "hello rotket mq!";
        //publisher.publish(Topic.DEMO.toString(), Tags.HELLO.toString(), msg);
        publisher.publish(Topic.GZLZTEST_0524.toString(), Tags.HELLO.toString(), msg);
        //waitConsume(20);
    }


}
