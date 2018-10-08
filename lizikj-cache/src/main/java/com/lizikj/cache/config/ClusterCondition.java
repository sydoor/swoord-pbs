package com.lizikj.cache.config;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Michael.Huang
 * @date 2017/7/5 19:11
 */
public class ClusterCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String env = conditionContext.getEnvironment().getProperty(Constants.CACHE_ENV);
        return Constants.CLUSTER.equals(env);
    }
}
