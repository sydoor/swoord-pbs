package com.lizikj.mq.demo;

import java.util.Properties;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

public class ConsumerTest {
//    public static void main(String[] args) {
//        Properties properties = new Properties();
//        properties.put(PropertyKeyConst.ConsumerId, "CID_GZLZTEST_0524");
//        properties.put(PropertyKeyConst.AccessKey, "LTAIObtlCQ4TbmbR");
//        properties.put(PropertyKeyConst.SecretKey, "wluZR417bu2jmGoT3PQ4GckNvmtqBS");
//        Consumer consumer = ONSFactory.createConsumer(properties);
//        consumer.subscribe("GZLZTEST_0524", "*", new MessageListener() {
//            public Action consume(Message message, ConsumeContext context) {
//                System.out.println("Receive: " + message);
//                return Action.CommitMessage;
//            }
//        });
//        consumer.start();
//        System.out.println("Consumer Started");
//    }
}    