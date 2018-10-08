package com.lizikj.mq.rocket.core;


import org.apache.rocketmq.common.message.MessageExt;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public interface RocketMessageRegister {

    boolean hasConsumed(MessageExt msg);

    void markConsumed(MessageExt msg);

}
