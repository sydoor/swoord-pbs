package com.lizikj.mq.aliware.config;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.lizikj.mq.aliware.core.AliwareForceBindKeysProducer;
import com.lizikj.mq.common.AliwaremqCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Configuration
@Conditional(AliwaremqCondition.class)
public class AliwareProducerConfig {

    @Autowired(required = false)
    private AliwareProperties aliwareConfig;

    @Bean(destroyMethod = "shutdown")
    public ProducerBean defaultAliwareMQProducer() {

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ProducerId, aliwareConfig.getProducerId());
        properties.put(PropertyKeyConst.AccessKey, aliwareConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, aliwareConfig.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr,
                aliwareConfig.getOnsAddr());//此处以公有云生产环境为例
        ProducerBean mqProducer = new AliwareForceBindKeysProducer(properties);
        return mqProducer;
    }

}
