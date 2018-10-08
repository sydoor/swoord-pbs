package com.lizikj.version;

import com.lizikj.common.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Michael.Huang
 * @date 2017/6/16
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.lizikj.version.annotation", "com.lizikj.version.service.impl"})
public class Bootstrap {
    private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        ApplicationContext ctx = new SpringApplicationBuilder()
                .sources(Bootstrap.class).web(false).run(args);
        logger.info("lizikj-version-test项目启动成功");
        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        closeLatch.await();
    }

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }

}
