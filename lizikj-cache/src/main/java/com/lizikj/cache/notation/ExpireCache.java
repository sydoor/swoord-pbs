package com.lizikj.cache.notation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExpireCache {

    /**
     * 缓存中key的前缀
     */
    String prefix();

    /**
     * 指定的可以作为key的参数字段，会获取对应key的参数value
     * 开启此参数需要java1.8支持,并开启javac编译参数-parameters
     * @since 1.8
     */
    String[] paramKeys() default {};
}