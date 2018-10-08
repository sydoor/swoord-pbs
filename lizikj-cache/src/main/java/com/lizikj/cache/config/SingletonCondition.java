package com.lizikj.cache.config;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Michael.Huang
 * @date 2017/7/5 19:03
 */
public class SingletonCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String env = conditionContext.getEnvironment().getProperty(Constants.CACHE_ENV);
        return Constants.SINGLETON.equals(env);
    }
}
