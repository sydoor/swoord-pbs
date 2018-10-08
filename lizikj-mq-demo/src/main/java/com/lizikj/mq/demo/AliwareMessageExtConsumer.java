package com.lizikj.mq.demo;

import org.springframework.stereotype.Component;

import com.aliyun.openservices.ons.api.Message;
import com.lizikj.mq.constants.Tags;
import com.lizikj.mq.constants.Topic;
import com.lizikj.mq.consumer.MessageSubscriber;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Component
public class AliwareMessageExtConsumer extends MessageSubscriber {

    @Override
    public String followTags() {

        return Tags.EXT.toString();
    }

    @Override
    public String followTopic() {
       //return Topic.DEMO.toString();
       return Topic.GZLZTEST_0524.toString();
    }


    @Override
    public void consume(Message message) {

        System.out.println("aliware MessageExtConsumer:" + message.getTopic() + " " + message.getKey());
    }

    @Override
    public void consume(String content) {
        //keep empty

    }

}
