package com.lizikj.mq.demo;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import com.lizikj.mq.constants.Tags;
import com.lizikj.mq.constants.Topic;
import com.lizikj.mq.consumer.MessageSubscriber;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Component
public class MessageExtConsumer extends MessageSubscriber {

    @Override
    public String followTags() {

        return Tags.EXT.toString();
    }

    @Override
    public String followTopic() {
        return Topic.DEMO.toString();
    }


    @Override
    public void consume(MessageExt message) {

        System.out.println("MessageExtConsumer:" + message.getTopic() + " " + message.getKeys());
    }

    @Override
    public void consume(String content) {
        //keep empty

    }

}
