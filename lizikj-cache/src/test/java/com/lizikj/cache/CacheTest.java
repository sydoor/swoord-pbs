package com.lizikj.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lizikj.cache.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @SpringApplicationConfiguration 在1.4开始就被替换 @SpringBootTest
 * Created by zhoufe on 2017/4/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("redis.singleton")
public class CacheTest {
	
	private static final Logger logger = LoggerFactory.getLogger(CacheTest.class);

    @Autowired
    private Cache cache;

    @Test
    public void testScan() throws Exception{
        cache.scan("push:*",100);
    }

    @Test
    public void test() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("hey", "there");
        map.put("what", "'up");
        map.put("hello", "world!");
        cache.multiSet(map);
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            cache.set(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }
        HashSet<String> strings = new HashSet<>();
        strings.add("hey");
        strings.add("what");
        strings.add("hello");
        Map<String, Object> stringObjectMap = cache.multiGet(strings);
        logger.info("{}", stringObjectMap);
        cache.set("hello", "world");
        logger.info("{}", cache.hasKey("hello"));
        cache.set("what", "the fuck");
        logger.info("{}", cache.get("what"));
        logger.info("{}", cache.getAndSet("what", "the fuck"));
        logger.info("{}", cache.get("what"));
        cache.setIfAbsent("what", "the hell");
        logger.info("{}", cache.get("what"));
        Set<String> keys = cache.keys("*");
        logger.info("keys:{}", keys);
        
    }

}
