package com.lizikj.mq.producer;

import com.lizikj.mq.common.MQSendResult;
import com.lizikj.mq.exception.MQException;

import java.io.Serializable;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public interface MessagePublisher {

    MQSendResult publish(String topic, String tags, String content) throws MQException;

    MQSendResult publish(String topic, String tags, byte[] content) throws MQException;

    MQSendResult publish(String topic, String tags, Serializable object) throws MQException;

}
