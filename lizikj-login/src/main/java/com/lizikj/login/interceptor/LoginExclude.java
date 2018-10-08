package com.lizikj.login.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 过滤不登录拦截的注解类
 * @author lijundong 
 * @date 2017年8月30日 下午7:58:03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface LoginExclude {

}
