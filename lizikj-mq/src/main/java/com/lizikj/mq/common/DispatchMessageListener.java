package com.lizikj.mq.common;

import com.lizikj.mq.consumer.MessageSubscriber;

public interface DispatchMessageListener {

	void register(MessageSubscriber messageSubscriber);

}
