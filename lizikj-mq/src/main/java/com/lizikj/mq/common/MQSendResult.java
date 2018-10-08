package com.lizikj.mq.common;

import com.aliyun.openservices.ons.api.SendResult;

/**
 * 发送消息返回的结果：根据不同的环境返回不同的类型，用的时候自己取
 * @author zhoufe 2017年5月22日 下午8:53:15
 *
 */
public class MQSendResult {

	
	private SendResult sendResult;
	private org.apache.rocketmq.client.producer.SendResult rocketSendResult;

	public MQSendResult(SendResult sendResult) {
		this.sendResult = sendResult;
	}

	public MQSendResult(org.apache.rocketmq.client.producer.SendResult sendResult) {
		this.rocketSendResult = sendResult;
	}

	public SendResult getSendResult() {
		return sendResult;
	}

	public org.apache.rocketmq.client.producer.SendResult getRocketSendResult() {
		return rocketSendResult;
	}
	
	

}
