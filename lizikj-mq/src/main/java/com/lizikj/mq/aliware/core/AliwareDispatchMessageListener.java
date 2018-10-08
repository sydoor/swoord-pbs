package com.lizikj.mq.aliware.core;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.lizikj.mq.common.AliwaremqCondition;
import com.lizikj.mq.common.DispatchMessageListener;
import com.lizikj.mq.constants.Constants;
import com.lizikj.mq.consumer.MessageSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Michael.Huang on 2017/4/1.
 * 分发处理消息监听器
 */
@Component
@Conditional(AliwaremqCondition.class)
public class AliwareDispatchMessageListener implements MessageListener, DispatchMessageListener {

    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, MessageSubscriber> consumers = new HashMap<>();

    @Value("${mq.env:PROD}")
    private String mqEnvironment;

    @Override
    public void register(MessageSubscriber consumer) {
        String topicName = consumer.followTopic().toString() + "_" + mqEnvironment.toUpperCase();
        String key = Constants.genKey(topicName, consumer.followTags().toString());
        consumers.put(key, consumer);
        log.info("aliware mq 注册成功! consumer={} topic={} tags={}",
                new Object[]{consumer.getClass().getSimpleName(), topicName, consumer.followTags()});
    }


    @Override
    public Action consume(Message message, ConsumeContext context) {
        try {
            MessageSubscriber aliwareConsumer = consumers.get(Constants.genKey(message.getTopic(), message.getTag()));
            aliwareConsumer.consume(message);
        } catch (Exception e) {
            log.warn("aliware mq  消息处理异常。", e);
            log.warn("aliware mq  失败消息信息:topic={} tags={} keys={} ",
                    new Object[]{message.getTopic(), message.getTag(), message.getKey()});
            return Action.ReconsumeLater;
        }

        return Action.CommitMessage;
    }


}
