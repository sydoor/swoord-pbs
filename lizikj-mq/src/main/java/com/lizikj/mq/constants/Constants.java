package com.lizikj.mq.constants;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.lizikj.common.util.DateUtils;
import com.lizikj.mq.aliware.config.AliwareProperties;

import java.util.Properties;
import java.util.Random;

/**
 * 常量文件
 *
 * @author zhoufe 2017年5月23日 上午10:28:18
 */
public class Constants {
    public static final String MQ_ENV = "mq.env";

    /**
     * 发送消息的标识
     *
     * @return
     */
    public static String genKeys() {
        return "MSG" + DateUtils.getCurrent() + (1000 + new Random().nextInt(9000));
    }

    /**
     * 获取注册的consumer用的key
     *
     * @param topic
     * @param tags
     * @return
     */
    public static String genKey(String topic, String tags) {
        return topic + "_" + tags;
    }

    public static boolean isProdEnv(String mqEnvironment) {
        return MQEnvironmentEnum.PROD.toString().toLowerCase().equals(mqEnvironment);
    }

    public static boolean isPreEnv(String mqEnvironment) {
        return MQEnvironmentEnum.TEST.toString().toLowerCase().equals(mqEnvironment);
    }

    public static boolean isTestEnv(String mqEnvironment) {
        return MQEnvironmentEnum.TEST.toString().toLowerCase().equals(mqEnvironment);
    }

    public static boolean isDevEnv(String mqEnvironment) {
        return MQEnvironmentEnum.DEV.toString().toLowerCase().equals(mqEnvironment);
    }

    public static Properties getAliwareConsumerProperties(AliwareProperties aliwareConfig) {
        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.ConsumerId, aliwareConfig.getConsumerId());
        setAliwareAuth(aliwareConfig, producerProperties);
        return producerProperties;
    }

    public static Properties getAliwareProudcerProperties(AliwareProperties aliwareConfig) {
        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.ProducerId, aliwareConfig.getProducerId());
        setAliwareAuth(aliwareConfig, producerProperties);
        return producerProperties;
    }

    private static void setAliwareAuth(AliwareProperties aliwareConfig, Properties producerProperties) {
        producerProperties.setProperty(PropertyKeyConst.AccessKey, aliwareConfig.getAccessKey());
        producerProperties.setProperty(PropertyKeyConst.SecretKey, aliwareConfig.getSecretKey());
        producerProperties.setProperty(PropertyKeyConst.ONSAddr, aliwareConfig.getOnsAddr());
    }
}
