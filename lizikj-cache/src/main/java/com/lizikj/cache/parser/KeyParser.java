package com.lizikj.cache.parser;

import org.aspectj.lang.reflect.MethodSignature;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public interface KeyParser {

    /**
     * parse string into keys
     *
     * @param key
     * @return
     */
    String parse(String key, Object[] args, MethodSignature methodSignature) throws IntrospectionException, InvocationTargetException, IllegalAccessException;

    /**
     * determine whether it's variable
     *
     * @param key
     * @return
     */
    boolean isVariable(String key);
}
