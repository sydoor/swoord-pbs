package com.lizikj.mq.rocket.core;

import com.lizikj.mq.common.DispatchMessageListener;
import com.lizikj.mq.common.RocketmqCondition;
import com.lizikj.mq.constants.Constants;
import com.lizikj.mq.consumer.MessageSubscriber;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Michael.Huang on 2017/4/1.
 * 分发处理消息监听器
 */
@Component
@Conditional(RocketmqCondition.class)
public class RocketDispatchMessageListener implements MessageListenerConcurrently, DispatchMessageListener {

    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, MessageSubscriber> consumers = new HashMap<>();

    @Value("${mq.env:PROD}")
    private String mqEnvironment;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus reconsumeLater = ConsumeConcurrentlyStatus.RECONSUME_LATER;
        if (msgs == null || msgs.size() == 0) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt msg = msgs.get(0);
        log.info("rocket mq receive message: topic={} tags={} keys={}",
                new Object[]{msg.getTopic(), msg.getTags(), msg.getKeys()});
        String key = Constants.genKey(msg.getTopic(), msg.getTags());
        MessageSubscriber consumer = consumers.get(key);
        if (consumer == null) {
            log.info("rocket mq  fail to found consumer. topic={} tags={}", msg.getTopic(), msg.getTags());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        try {
            consumer.consume(msg);
            log.info("rocket mq  msg({}) consume sucessfully!", msg.getKeys());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.warn("rocket mq  消息处理异常。", e);
            log.warn("rocket mq  失败消息信息:topic={} tags={} keys={} ",
                    new Object[]{msg.getTopic(), msg.getTags(), msg.getKeys()});
            return reconsumeLater;
        }

    }

    @Override
    public void register(MessageSubscriber consumer) {
        String topicName = consumer.followTopic().toString() + "_" + mqEnvironment.toUpperCase();
        String key = Constants.genKey(topicName, consumer.followTags().toString());
        consumers.put(key, consumer);
        log.info("rocket mq 注册成功! consumer={} topic={} tags={}",
                new Object[]{consumer.getClass().getSimpleName(), topicName, consumer.followTags()});
    }

}
