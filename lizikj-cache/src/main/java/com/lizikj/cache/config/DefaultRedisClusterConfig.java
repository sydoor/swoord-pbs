package com.lizikj.cache.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Configuration
// @PropertySource("classpath:cache.properties")
// @Profile("redis.cluster")
@Conditional(value = ClusterCondition.class)
public class DefaultRedisClusterConfig {

	private static final Logger logger = LoggerFactory.getLogger(DefaultRedisClusterConfig.class);

	@Value("${spring.redis.cluster.nodes}")
	private String clusterNodes;
	@Value("${spring.redis.cluster.max-redirects}")
	private int redirects;
	@Value("${spring.redis.pool.max-idle}")
	private int maxIdle;
	@Value("${spring.redis.pool.min-idle}")
	private int minIdle;
	@Value("${spring.redis.pool.max-total}")
	private int maxTotal;
	@Value("${spring.redis.cluster.password}")
	private String password;

	@Bean
	public RedisConnectionFactory connectionFactory() {
		Map<String, Object> source = new HashMap<>();
		source.put("spring.redis.cluster.nodes", clusterNodes);
		source.put("spring.redis.cluster.max-redirects", redirects);
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(
				new MapPropertySource("redisClusterConfiguration", source));

		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		jedisConnectionFactory.setPassword(password);// 集群都用同一个？
		return jedisConnectionFactory;
	}

	@Bean
	public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		logger.info("开始配置redisTemplate");
		RedisTemplate redistemplate = new RedisTemplate();
		redistemplate.setConnectionFactory(redisConnectionFactory);

		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		redistemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redistemplate.setKeySerializer(new StringRedisSerializer());
		return redistemplate;
	}
}
