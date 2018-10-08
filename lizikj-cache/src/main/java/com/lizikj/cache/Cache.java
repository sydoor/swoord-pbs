package com.lizikj.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public interface Cache<T> {
	/**
	 * 加锁
	 * @param lockKey
	 * @param requestId
	 * @param tryLockTimeout
	 * @return
	 */
	boolean lock(String lockKey,String requestId,long tryLockTimeout);

	/**
	 * 加锁
	 * @param lockKey
	 * @param tryLockTimeout
	 * @return
	 */
	String lock(String lockKey,long tryLockTimeout);

	/**
	 * 获取锁
	 * @param lockKey
	 * @param lockTimeout
	 * @param tryLockTimeout
	 * @return
	 */
	String lock(String lockKey,long lockTimeout, long tryLockTimeout);

	/**
	 * 一次性获取锁
	 * 没有获取到就返回空
	 * @param lockKey
	 * @return
	 */
	String tryLockOnce(String lockKey);

	/**
	 * 解锁
	 * @param lockKey
	 * @param requestId
	 * @return
	 */
	boolean unlock(String lockKey,String requestId);

	Set<String> scan(String pattern,int count);

	/**
	 * 追加内容
	 * @param key
	 * @param value
	 */
	void append(String key, String value);

	/**
	 * 增长
	 * @param key
	 * @param delta
	 */
	long increment(String key, long delta);

	/**
	 * 增长
	 * @param key
	 * @param delta
	 */
	double increment(String key, double delta);

	/**
	 * 设置键值对
	 * @param key
	 * @param value
	 */
	void set(String key, Object value);

	/**
	 * 设置带有过期时间的键值对
	 * @param key
	 * @param value
	 * @param timeout
	 * @param timeUnit
	 */
	void set(String key, Object value, long timeout, TimeUnit timeUnit);

	/**
	 * 对 key 所储存的字符串值，设置或清除指定偏移量上的位 bitmap set
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	boolean setBit(String key,long offset,boolean value);

	/**
	 * 获取键对应值的ascii码的在offset处位值
	 * @param key
	 * @param offset
	 * @return
	 */
	boolean getBit(String key,long offset);
	/**
	 * 仅当不存在时设置键值对
	 * @param key
	 * @param value
	 * @return
	 */
	boolean setIfAbsent(String key, Object value);

	/**
	 * 获取键对应的值
	 * @param key
	 * @return
	 */
	Object get(String key);

	/**
	 * 获取键对应的值
	 * 返回String
	 * @param key
	 * @return
	 */
	String getAsStr(String key);

	/**
	 * 获取键对应的过期时间
	 * @param key
	 * @return
	 */
	long getExpire(String key);

	/**
	 * 对键设置过期时间
	 * @param key
	 * @param timeout
	 * @param timeUnit
	 */
	void expire(String key, long timeout, TimeUnit timeUnit);

	/**
	 * 获取键对应的值，并赋予新值
	 * @param key
	 * @param value
	 * @return
	 */
	Object getAndSet(String key, Object value);

	/**
	 * 获取pattern对应的键
	 * @param pattern
	 * @return
	 */
	Set<String> keys(String pattern);

	/**
	 * 查询是否含有该键
	 * @param key
	 * @return
	 */
	boolean hasKey(String key);

	/**
	 * 删除键
	 * @param key
	 */
	void delete(String key);

	/**
	 * 批量设置键值对
	 * @param paramMap
	 */
	void multiSet(Map<String, Object> paramMap);

	/**
	 * 批量获取键值对
	 * @param keys
	 * @return
	 */
	Map<String, Object> multiGet(Set<String> keys);

	/**
	 * 添加成员到集合
	 * @param key
	 * @param value
	 * @return
	 */
	Long sadd(String key, Object... value);
	
	/**
	 * 移除某些成员出集合
	 * @param key
	 * @param value
	 * @return
	 */
	Long srem(String key, Object... value);
	
	/**
	 * 移除并返回集合中的一个随机元素
	 * @param key
	 * @return
	 */
	Object spop(String key);
	
	/**
	 * 获取集合的成员数
	 * @param key
	 * @return
	 */
	Long scard(String key);

	Set<Object> members(String key);

	/**
	 * 获取底层操作类
	 * @return
	 */
	T getCore();
	
	/**
	 * 将哈希表 key 中的域 hashKey 的值设为 value.
	 */
	public void hset(String key, Object hashKey, Object value);

	
	/**
	 * 返回哈希表 key 中给定域 hashKey 的值.
	 */
	public Object hget(String key, Object hashKey);

	
	/**
	 * 将哈希表 key 中的域 hashKey 的值设置为 value ，当且仅当域 hashKey 不存在.
	 */
	public void hsetnx(String key, Object hashKey, Object value);

	
	/**
	 * 同时将多个 hashKey-value (域-值)对设置到哈希表 key 中.
	 */
	public void hmset(String key, Map<String, Object> hash);

	
	/**
	 * 返回哈希表 key 中，一个或多个给定域的值.
	 */
	public List<Object> hmget(String key, List<Object> hashKeys);

	
	/**
	 * 判断哈希表 key 中，给定域 hashKey 是否存在.
	 */
	public Boolean hexists(String key, Object hashKey);

	
	/**
	 * 删除哈希表 key 中的一个或多个指定域.
	 */
	public Long hdel(String key, Object... hashKeys);

	
	/**
	 * 返回哈希表 key 中域的数量.
	 */
	public Long hlen(String key);

	
	/**
	 * 返回哈希表 key 中的所有域.
	 */
	public Set<Object> hkeys(String key);

	
	/**
	 * 返回哈希表 key 中所有域的值.
	 */
	public List<Object> hvals(String key);

	
	/**
	 * 返回哈希表 key 中，所有的域和值.
	 */
	public Map<Object, Object> hgetAll(String key);
	
	
	/**
	 * 将元素及其 score 值加入到有序集 key 中.
	 */
	public boolean zadd(String key, double score, Object value);
	
	/**
	 * 移除有序集 key 中的一个或多个成员.
	 */
	public Long zrem(String key, Object... values);

	/**
	 * 返回有序集 key的数量
	 */
	public Long zcard(String key);
	
	/**
	 * 返回有序集 key 中，指定区间内的成员，有序集成员按 score 值递减(从大到小)次序排列.
	 */
	public Set<Object> zrevrange(String key, long start, long end);

	/**
	 * 返回有序集 key 中，指定区间内的成员，其中成员的位置按 score 值递增(从小到大)来排序.
	 */
	public Set<Object> zrange(String key, long start, long end);
	
	/**
	 * 返回有序集合中指定成员的索引
	 */
	public Long zrank(String key, Object value);
	
	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间的成员，
	 * 有序集成员按 score 值递增(从小到大)次序排列.
	 */
	public Set<Object> zrangeByScore(String key, double min, double max);
	
	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间的成员，
	 * 有序集成员按 score 值递减(从大到小)次序排列.
	 */
	public Set<Object> zrevrangeByScore(String key, double min, double max);

	/**
	 * 放入队列到表头
	 */
	public long lpush(final String key, Object obj);

	/**
	 * 放入队列到表头
	 * @param key
	 * @param objectList
	 * @return
	 */
	long lpushAll(final String key, Collection<Object> objectList);

	/**
	 * 放入队列到表头：如果不存在的key，什么也不做
	 */
	public long lpushx(final String key, Object obj);

	/**
	 * 放入队列到表尾
	 */
	public long rpush(final String key, Object obj);

	/**
	 * 放入队列到表尾
	 * @param key
	 * @param objectList
	 * @return
	 */
	long rpushAll(final String key,Collection<Object> objectList);

	/**
	 * 放入队列到表尾：如果不存在的key，什么也不做
	 */
	public long rpushx(final String key, Object obj);

	/**
	 * 从表头弹出队列中的元素
	 */
	public Object lpop(final String key);

	/**
	 * 从表尾弹出队列中的元素
	 */
	public Object rpop(final String key);

	/**
	 * 返回列表 key 的长度。
	 * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
	 * 如果 key 不是列表类型，返回一个错误。
	 * @param key
	 * @return
	 */
	public Long llen(final String key);

}
