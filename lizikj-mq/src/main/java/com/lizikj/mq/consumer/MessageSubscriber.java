package com.lizikj.mq.consumer;

import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aliyun.openservices.ons.api.Message;
import com.lizikj.mq.common.DispatchMessageListener;

public abstract class MessageSubscriber {

	protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private DispatchMessageListener dispatchMessageListener;
    
    public abstract String followTags();

    public abstract String followTopic();
    /**
     * 不能重复消费的子类可重写该方法
     * @return
     */
    /**
     * 不再支持，消费端保持冥等
     */
//		public boolean recallable(){
//			return true;
//		};
    @PostConstruct
    public void register() {
    	dispatchMessageListener.register(this);
    }

    /**
     * 要获取完整message可在子类重写该方法
     *
     * @param message
     */
    public void consume(MessageExt message) {
        String content = new String(message.getBody(), Charset.defaultCharset());
        log.info("rocket content={}", content);
        this.consume(content);
    }

    /**
     * 要获取完整message可在子类重写该方法
     *
     * @param message
     */
    public void consume(Message message) {
        String content = new String(message.getBody(), Charset.defaultCharset());
        log.info("aliware content={}", content);
        this.consume(content);
    }
    
    public abstract void consume(String content);
}
