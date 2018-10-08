package com.lizikj.mq.rocket.core;


import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import com.lizikj.mq.constants.Constants;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class RocketForceBindKeysProducer extends TransactionMQProducer {

    @Override
    public TransactionSendResult sendMessageInTransaction(Message msg,
                                                          LocalTransactionExecuter tranExecuter, Object arg)
            throws MQClientException {
        addKeysWhenNotExist(msg);
        return super.sendMessageInTransaction(msg, tranExecuter, arg);
    }


    @Override
    public void sendOneway(Message msg) throws MQClientException,
            RemotingException, InterruptedException {
        addKeysWhenNotExist(msg);
        super.sendOneway(msg);
    }


    @Override
    public SendResult send(Message msg) throws MQClientException,
            RemotingException, MQBrokerException, InterruptedException {
        addKeysWhenNotExist(msg);
        return super.send(msg);
    }


    @Override
    public SendResult send(Message msg, long timeout) throws MQClientException,
            RemotingException, MQBrokerException, InterruptedException {
        addKeysWhenNotExist(msg);
        return super.send(msg, timeout);
    }

    private void addKeysWhenNotExist(Message msg) {
        if (msg.getKeys() == null) {
            msg.setKeys(Constants.genKeys());
        }

    }

}
