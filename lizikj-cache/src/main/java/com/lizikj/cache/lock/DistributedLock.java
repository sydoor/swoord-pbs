package com.lizikj.cache.lock;

import com.lizikj.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michael.Huang on 2017/4/1.
 * 基于redis实现的分布式锁
 */
@Component
public class DistributedLock {
    //加锁标志
    public static final String LOCKED = "TRUE";
    public static final long ONE_MILLI_NANOS = 1000000L;
    //默认超时时间（毫秒）
    public static final long DEFAULT_TIME_OUT = 10000;
    public static final Random r = new Random();
    //刪除 锁的超时时间（秒）
    public static final int EXPIRE = 5 * 60;
    protected static Logger logger = LoggerFactory.getLogger(DistributedLock.class);
    @Autowired
    private Cache cache;


    public boolean lock(long timeout, String key) {
        long nano = System.nanoTime();
        timeout *= ONE_MILLI_NANOS;
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (cache.setIfAbsent(key, LOCKED)) {
                    cache.set(key, LOCKED, EXPIRE, TimeUnit.SECONDS);
                    return true;
                }
                //短暂休眠，nano避免出现活锁
                Thread.sleep(3, r.nextInt(500));
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean lock(String key) {
        return lock(DEFAULT_TIME_OUT, key);
    }

    /**
     * 加锁成功后，需要在finally中调用
     *
     * @param key
     */
    public void unlock(String key) {
        cache.delete(key);
    }
}
