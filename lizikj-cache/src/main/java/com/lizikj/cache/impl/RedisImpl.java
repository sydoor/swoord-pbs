package com.lizikj.cache.impl;

import com.lizikj.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Component("cache")
@SuppressWarnings({"unchecked", "rawtypes"})
public class RedisImpl<K, V> implements Cache<RedisTemplate<K, V>> {
    private static final long DEFAULT_LOCK_EXPIRE = 5*60*1000;
    private static final Logger logger = LoggerFactory.getLogger(RedisImpl.class);
    private static final RedisScript<String> unlockScript =
            new DefaultRedisScript<>("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",String.class);

    private static final RedisScript<String> lockScript =
            new DefaultRedisScript<String>("return redis.call('SET', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2])",String.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean lock(String lockKey,String requestId,long tryLockTimeout){
        String lock = lock(lockKey, requestId, DEFAULT_LOCK_EXPIRE, tryLockTimeout);

        return lock == null ? false:true;
    }



    @Override
    public String lock(String lockKey,long tryLockTimeout){

        return lock(lockKey,UUID.randomUUID().toString(),DEFAULT_LOCK_EXPIRE,tryLockTimeout);
    }

    @Override
    public String lock(String lockKey, long lockTimeout, long tryLockTimeout) {
        String requestId = UUID.randomUUID().toString();

        return lock(lockKey,requestId,lockTimeout,tryLockTimeout);
    }

    private String lock(String lockKey,String requestId,long lockTimeout, long tryLockTimeout){
        long start = System.currentTimeMillis();

        while(tryLockTimeout == 0 || (System.currentTimeMillis() - start) < tryLockTimeout){
            Object result = redisTemplate.execute(lockScript,redisTemplate.getStringSerializer(), redisTemplate.getStringSerializer(),
                    Collections.singletonList(lockKey), requestId, String.valueOf(lockTimeout));
            if(result != null && "OK".equals(String.valueOf(result))){

                return requestId;
            }else{
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    logger.info("休眠中断 ",e);
                }
            }
        }

        return null;
    }

    @Override
    public String tryLockOnce(String lockKey){
        String requestId = UUID.randomUUID().toString();

        Object result = redisTemplate.execute(lockScript,redisTemplate.getStringSerializer(), redisTemplate.getStringSerializer(),
                Collections.singletonList(lockKey), requestId, String.valueOf(DEFAULT_LOCK_EXPIRE));
        if(result != null && "OK".equals(String.valueOf(result))){

            return requestId;
        }

        return null;
    }



    @Override
    public boolean unlock(String lockKey,String requestId){
        Object result = redisTemplate.execute(unlockScript, redisTemplate.getStringSerializer(), redisTemplate.getStringSerializer(),
                Collections.singletonList(lockKey), requestId);
        if(result != null && "1".equals(String.valueOf(result))){
            return true;
        }

        return false;
    }

    @Override
    public Set<String> scan(String pattern, int count) {
        final ScanOptions options = ScanOptions.scanOptions().count(count).match(pattern).build();
        Object execute = null;
        try {
            execute = redisTemplate.execute(new RedisCallback<List<byte[]>>() {
                @Override
                public List<byte[]> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    List<byte[]> binaryKeys = new ArrayList<>();
                    Cursor<byte[]> cursor = redisConnection.scan(options);
                    while (cursor.hasNext()) {
                        binaryKeys.add(cursor.next());
                    }

                    try {
                        redisConnection.close();
                    } catch (Exception e) {
                        logger.info("redis close exception {}", e);
                    }

                    return binaryKeys;
                }
            });
        } catch (Exception e) {
            logger.info("redis scan exception {} {}",pattern, e);
        }

        Set<String> result = new HashSet<>();


        if(execute != null && execute instanceof List){
            List<byte[]> temps = (List<byte[]>)execute;
            for (byte[] item :temps){
                result.add(new String(item));
            }
        }

        return result;
    }

    @Override
    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }

    @Override
    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public boolean setBit(String key, long offset, boolean value) {
        Boolean aBoolean = redisTemplate.opsForValue().setBit(key, offset, value);
        if(aBoolean == null){
            return false;
        }

        return aBoolean;
    }

    @Override
    public boolean getBit(String key, long offset) {
        Boolean bit = redisTemplate.opsForValue().getBit(key, offset);
        if(bit == null){
            return false;
        }

        return bit;
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public String getAsStr(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    @Override
    public long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    @Override
    public Object getAndSet(String key, Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    @Override
    public Set<String> keys(String pattern) {
        Set<String> keys = null;

        try {
            keys = clusterKeys(pattern);
        } catch (Exception e) {
            logger.warn("cluster exception:{}", e.getMessage());

            keys = singletonKeys(pattern);
        }

        return keys;
    }

    private Set<String> singletonKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        RedisConnection redisConnection = null;
        try {
            redisConnection = redisTemplate.getConnectionFactory().getConnection();
            Set<byte[]> byteKeys = redisConnection.keys(pattern.getBytes());
            if (byteKeys != null && !byteKeys.isEmpty()) {
                for (byte[] byteKey : byteKeys) {
                    if (byteKey != null && byteKey.length > 0) {
                        keys.add(new String(byteKey));
                    }
                }
            }
            return keys;
        } catch (Exception e) {
            throw e;
        } finally {
            if (redisConnection != null) {
                redisConnection.close();
            }
        }
    }

    private Set<String> clusterKeys(String pattern) throws Exception {
        Set<String> keys = new HashSet<>();
        RedisClusterConnection clusterConnection = null;
        try {
            clusterConnection = redisTemplate.getConnectionFactory().getClusterConnection();
            Iterable<RedisClusterNode> redisClusterNodes = clusterConnection.clusterGetNodes();
            Set<byte[]> keysInByte = new HashSet<>();
            for (RedisClusterNode redisClusterNode : redisClusterNodes) {
                Set<byte[]> keysInNode;
                try {
                    System.out.println(redisClusterNode);
                    System.out.println("===" + clusterConnection.ping(redisClusterNode) + "===");
                    keysInNode = clusterConnection.keys(redisClusterNode, pattern.getBytes());
                    if (isEmpty((keysInNode))) {
                        continue;
                    }
                    keysInByte.addAll(keysInNode);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            for (byte[] bytes : keysInByte) {
                keys.add(new String(bytes));
            }
            return keys;
        } catch (Exception e) {
            throw e;
        } finally {
            if (clusterConnection != null) {
                clusterConnection.close();
            }
        }
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void multiSet(Map<String, Object> paramMap) {
        if (isEmpty(paramMap)) {
            return;
        }
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            redisTemplate.opsForValue().set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<String, Object> multiGet(Set<String> keys) {
        Map<String, Object> resultMap = new HashMap<>();
        if (isEmpty(keys)) {
            return Collections.EMPTY_MAP;
        }
        for (String key : keys) {
            resultMap.put(key, redisTemplate.opsForValue().get(key));
        }
        return resultMap;
    }

    @Override
    public Long sadd(String key, Object... value) {
        return redisTemplate.boundSetOps(key).add(value);
    }

    @Override
    public Set<Object> members(String key) {
        return redisTemplate.boundSetOps(key).members();
    }

    @Override
    public RedisTemplate<K, V> getCore() {
        return redisTemplate;
    }

    @Override
    public void hset(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Object hget(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public void hsetnx(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    @Override
    public void hmset(String key, Map<String, Object> hash) {
        redisTemplate.opsForHash().putAll(key, hash);
    }

    @Override
    public List<Object> hmget(String key, List<Object> hashKeys) {
        return redisTemplate.opsForHash().multiGet(hashKeys, hashKeys);
    }

    @Override
    public Boolean hexists(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public Long hdel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    @Override
    public Long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    @Override
    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    @Override
    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    @Override
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public boolean zadd(String key, double score, Object value) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public Long zrem(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    @Override
    public Long zcard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    @Override
    public Set<Object> zrevrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    @Override
    public Set<Object> zrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    @Override
    public Long zrank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    @Override
    public Set<Object> zrangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    @Override
    public Set<Object> zrevrangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    @Override
    public Long srem(String key, Object... value) {
        return redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public Object spop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    @Override
    public Long scard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public long lpush(final String key, Object obj) {
        return redisTemplate.opsForList().leftPush(key, obj);
    }

    @Override
    public long lpushAll(final String key, Collection<Object> objectList) {

        return redisTemplate.opsForList().leftPushAll(key,objectList);
    }

    @Override
    public long lpushx(String key, Object obj) {
        return redisTemplate.opsForList().leftPushIfPresent(key, obj);
    }

    @Override
    public long rpush(final String key, Object obj) {
        return redisTemplate.opsForList().rightPush(key, obj);
    }

    @Override
    public long rpushAll(String key, Collection<Object> objectList) {

        return redisTemplate.opsForList().rightPushAll(key,objectList);
    }

    @Override
    public long rpushx(String key, Object obj) {
        return redisTemplate.opsForList().rightPushIfPresent(key, obj);
    }

    @Override
    public Object lpop(final String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public Object rpop(final String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public Long llen(final String key) {
        return redisTemplate.opsForList().size(key);
    }

}
