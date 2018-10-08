package com.lizikj.cache.notation;


import com.lizikj.cache.aspect.CacheAOP;

import java.lang.annotation.*;

/**
 * Created by Michael.Huang on 2017/4/1.
 * 获取数据时的缓存注解。
 * 缓存中有值时，则取缓存中的值；
 * 缓存中无值时，则从数据库中取值并放入到缓存中。
 *
 * @see CacheAOP
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Inherited
public @interface GetCache {

    /**
     * 缓存中key的前缀
     */
    String prefix();

    /**
     * 过期时间，不设置则不过期
     */
    long expire() default -1;

    /**
     * 指定的可以作为key的参数字段，会获取对应key的参数value
     * 开启此参数需要java1.8支持,并开启javac编译参数-parameters
     * @since 1.8
     */
    String[] paramKeys() default {};
}
