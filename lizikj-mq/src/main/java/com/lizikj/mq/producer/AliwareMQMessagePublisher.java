package com.lizikj.mq.producer;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.lizikj.common.util.DateUtils;
import com.lizikj.mq.common.AliwaremqCondition;
import com.lizikj.mq.common.MQSendResult;
import com.lizikj.mq.exception.MQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by zhoufe on 2017/5/20.
 */
@Service
@Conditional(AliwaremqCondition.class)
public class AliwareMQMessagePublisher implements MessagePublisher {
    protected Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    private Producer producer;

    @Value("${mq.env:PROD}")
    private String mqEnvironment;


    @Override
    public MQSendResult publish(String topic, String tags, Serializable object) throws MQException {
        //转为json，监控端可读
        return publish(topic, tags, JSON.toJSONString(object));
    }

    @Override
    public MQSendResult publish(String topic, String tags, String content) throws MQException {
        return publish(topic, tags, content.getBytes(Charset.defaultCharset()));

    }

    @Override
    public MQSendResult publish(String topic, String tags, byte[] content) throws MQException {

        String keys = genKeys();
        String suffix = "_" + mqEnvironment.toUpperCase();
        String topicName = topic.toString() + suffix;
        Message message = new Message(topicName, tags.toString(), keys, content);
        log.info("aliware mq message publishing... topic={} tags={} keys={}",
                new Object[]{topicName, tags, keys});
        try {
            SendResult sendResult = producer.send(message);
            log.info("aliware mq  message published. keys={} sendResult={}", keys, sendResult);

            return new MQSendResult(sendResult);
        } catch (Exception e) {
            log.error("aliware mq 发布异常. tags={} keys={}", tags, keys);
            throw new MQException("发布异常", e);
        }
    }

    private String genKeys() {

        return "MSG" + DateUtils.getCurrent() + (1000 + new Random().nextInt(9000));
    }


}
