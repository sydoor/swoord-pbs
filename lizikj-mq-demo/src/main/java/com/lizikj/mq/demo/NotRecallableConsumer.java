package com.lizikj.mq.demo;

import com.lizikj.mq.constants.Tags;
import com.lizikj.mq.constants.Topic;
import com.lizikj.mq.consumer.MessageSubscriber;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class NotRecallableConsumer extends MessageSubscriber {

    private int account = 100;

    @Override
    public String followTags() {

        return Tags.PAY.toString();
    }

    @Override
    public String followTopic() {

        return Topic.DEMO.toString();
    }

    @Override
    public void consume(String content) {
        account += Integer.valueOf(content);
        System.out.println("after consume:account=" + account);
    }
    //不再支持
//	@Override
//	public boolean recallable() {
//		
//		return false;
//	}


}
