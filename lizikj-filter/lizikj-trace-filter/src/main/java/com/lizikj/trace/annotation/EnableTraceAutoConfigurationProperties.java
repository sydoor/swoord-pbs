package com.lizikj.trace.annotation;

import java.lang.annotation.*;

/**
 * 日志追踪是否启用的注解
 * @auth zone
 * @date 2017-10-14
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableTraceAutoConfigurationProperties {
}
