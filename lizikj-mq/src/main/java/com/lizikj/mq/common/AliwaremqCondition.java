package com.lizikj.mq.common;

import com.lizikj.mq.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 根据配置加载aliwaremq
 *
 * @author zhoufe 2017年5月23日 上午10:00:08
 */
public class AliwaremqCondition implements Condition {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String env = context.getEnvironment().getProperty(Constants.MQ_ENV);
        boolean whichEnv = Constants.isDevEnv(env)||Constants.isTestEnv(env) || Constants.isPreEnv(env) || Constants.isProdEnv(env);
        logger.info("AliwaremqCondition mqEnvironment is:" + whichEnv);
        return whichEnv;
    }

}
