package com.lizikj.cache.aspect;

import com.alibaba.fastjson.JSONObject;
import com.lizikj.cache.Cache;
import com.lizikj.cache.notation.ExpireCache;
import com.lizikj.cache.notation.GetCache;
import com.lizikj.cache.parser.KeyParser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.ArrayUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;


/**
 * Created by Michael.Huang on 2017/4/1.
 * 缓存切面的逻辑实现
 */
@Aspect
@Component
public class CacheAOP {

    private static Logger logger = LoggerFactory.getLogger(CacheAOP.class);

    private static String SEPARATOR = "_";

    @Autowired
    private Cache cache;

    @Autowired
    private KeyParser keyParser;

    @Pointcut("@annotation(com.lizikj.cache.notation.GetCache)")
    public void getCache() {
    }

    @Pointcut("@annotation(com.lizikj.cache.notation.ExpireCache)")
    public void expireCache() {
    }

    @Around("getCache()")
    public Object aroundGet(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Class<?> classTarget = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = ms.getParameterTypes();
        Method targetMethod = classTarget.getMethod(method.getName(), parameterTypes);
        GetCache annotation = targetMethod.getAnnotation(GetCache.class);
        Object[] args = joinPoint.getArgs();
        String[] keys = annotation.paramKeys();
        String key = getKey(ms, args, keys);
        String prefix = annotation.prefix();
        String cacheKey = prefix + key;

        Object object = cache.get(cacheKey);
        if (object != null) {
            if (object instanceof Collection) {
                if (((Collection) object).size() != 0) {
                    return object;
                }
            } else {
                return object;
            }
        }

        Object proceed = joinPoint.proceed();
        if (proceed == null) {
            return null;
        }
        if (proceed instanceof Collection) {
            if (((Collection) proceed).size() == 0) {
                logger.debug("result is collection and size is zero!");
                return proceed;
            }
        }

        long expire = annotation.expire();
        if (expire == -1) {
            cache.set(cacheKey, proceed);
        } else {
            cache.set(cacheKey, proceed, expire, TimeUnit.SECONDS);
        }
        logger.info("『{}〓{}』 has been saved to cache and will be expired in {} sec", cacheKey, JSONObject.toJSONString(proceed), expire);
        return proceed;
    }


    @After("expireCache()")
    public void afterExpire(JoinPoint joinPoint) throws IllegalAccessException, IntrospectionException, InvocationTargetException,NoSuchMethodException {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();

        Class<?> classTarget = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = ms.getParameterTypes();
        Method targetMethod = classTarget.getMethod(method.getName(), parameterTypes);
        ExpireCache annotation = targetMethod.getAnnotation(ExpireCache.class);


        String prefix = annotation.prefix();
        String[] keys = annotation.paramKeys();
        Object[] args = joinPoint.getArgs();

        String key = getKey(ms, args, keys);
        String cacheKey = prefix + key;

        cache.delete(cacheKey);
        logger.info("『{}』 has been deleted from cache.", cacheKey);
    }

    /**
     * 获取key
     *
     * @param methodSignature
     * @param args
     * @param keys
     * @return
     */
    private String getKey(MethodSignature methodSignature, Object[] args, String[] keys) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        if (isEmpty(keys)) {
            logger.info("no paramKeys has been specified, all the args will be used as keys");
            return arrayToString(args);
        }
        if (isAllVariable(keys) && isEmpty(args)) {
            throw new RuntimeException("parameterless method is not allowed in all-variable scenario.");
        }
        StringBuilder keyBuilder = new StringBuilder();
        for (String key : keys) {
            String parsedKey = keyParser.parse(key, args, methodSignature);
            if (isBlank(parsedKey)) {
                continue;
            }
            keyBuilder.append(parsedKey).append(SEPARATOR);
        }
        if (isBlank(keyBuilder.toString())) {
            throw new RuntimeException("no paramKeys has been specified.");
        }

        return keyBuilder.substring(0, keyBuilder.length() - 1);
    }

    private boolean isAllVariable(String[] keys) {
        for (String key : keys) {
            if (!keyParser.isVariable(key)) {
                return false;
            }
        }
        return true;
    }

    private String arrayToString(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder array = new StringBuilder();
        for (Object arg : args) {
            if (null == arg){
                throw new RuntimeException("传入参数为空");
            }
            array.append(arg.toString()).append("_");
        }
        return array.substring(0, array.length() - 1);
    }


    public static void main(String[] args) {
        Object[] ob = {null};
        CacheAOP cacheAOP = new CacheAOP();
        cacheAOP.arrayToString(ob);
    }
}
