package com.lizikj.mq.common;

import com.lizikj.mq.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 根据配置加载rocketmq
 *
 * @author zhoufe 2017年5月23日 上午10:01:16
 */
public class RocketmqCondition implements Condition {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String env = context.getEnvironment().getProperty(Constants.MQ_ENV);
        boolean whichEnv = false;
        logger.info("RocketmqCondition mqEnvironment is:" + whichEnv);
        return whichEnv;
    }

}
