package com.lizikj.mq.aliware.core;


import java.util.Properties;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.lizikj.mq.constants.Constants;

/**
 * 用一定格式的key发送消息的生产者
 * @author zhoufe 2017年5月22日 下午8:56:49
 *
 */
public class AliwareForceBindKeysProducer extends ProducerBean {
	
	public AliwareForceBindKeysProducer(Properties producerProperties) {
		super.setProperties(producerProperties);
	}

	@Override
    public SendResult send(Message message) {
		addKeysWhenNotExist(message);
        return super.send(message);
    }

    @Override
    public void sendOneway(Message message) {
		addKeysWhenNotExist(message);
        super.sendOneway(message);
    }

    @Override
    public void sendAsync(Message message, SendCallback sendCallback) {
		addKeysWhenNotExist(message);
        super.sendAsync(message, sendCallback);
    }
	
    private void addKeysWhenNotExist(Message msg) {
        if (msg.getKey() == null) {
            msg.setKey(Constants.genKeys());
        }

    }

}
